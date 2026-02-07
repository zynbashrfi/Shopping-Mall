package service;

import domain.Cart;
import domain.CartItem;
import domain.Product;
import persistence.CartRepository;
import persistence.ProductRepository;

import java.util.List;

public class CheckoutService {
    private final CartRepository carts;
    private final ProductRepository products;

    public CheckoutService(CartRepository carts, ProductRepository products) {
        this.carts = carts;
        this.products = products;
    }

    public void checkout(String userId) {
        Cart cart = carts.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found."));

        if (cart.getItems() == null || cart.getItems().isEmpty())
            throw new IllegalArgumentException("Cart is empty.");

        List<Product> allProduct = products.findAll();

        // 1. validate:
        for (CartItem ci: cart.getItems()) {
            Product p = allProduct.stream()
                    .filter(prod -> prod.getId().equals(ci.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + ci.getProductId()));

            if (!p.isAvailableForClient())
                throw new IllegalArgumentException("Product not available: " + p.getTitle());
            if (ci.getQty() <= 0)
                throw new IllegalArgumentException("Invalid quantity for: " + p.getTitle());
            if (p.getStock() < ci.getQty())
                throw new IllegalArgumentException("Insufficient stock for: " + p.getTitle());
        }

        // 2. commit:
        for (CartItem ci2: cart.getItems()) {
            Product p = allProduct.stream()
                    .filter(prod -> prod.getId().equals(ci2.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + ci2.getProductId()));
            p.setStock(p.getStock() - ci2.getQty());
        }
        // 3. Clear cart:
        cart.getItems().clear();
        // 4. Persist
        products.saveAll(allProduct);
        persistCart(cart);
    }
    private void persistCart(Cart cart) {
        List<Cart> all = carts.findAll();

        boolean replaced = false;
        for(int i = 0; i<all.size(); i++) {
            if (all.get(i).getUserId().equals(cart.getUserId())) {
                all.set(i, cart);
                replaced = true;
                break;
            }
        }
        if (!replaced) { all.add(cart); }
        carts.saveAll(all);
    }
}