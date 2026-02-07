package service;

import domain.Cart;
import domain.CartItem;
import domain.Product;
import persistence.CartRepository;
import persistence.ProductRepository;

import java.util.List;

public class CartService {
    private final CartRepository carts;
    private final ProductRepository products;

    public CartService(CartRepository carts, ProductRepository products) {
        this.carts = carts;
        this.products = products;
    }

    public Cart getOrCreateCart(String userId) {
        return carts.findByUserId(userId).orElseGet(() -> {
            Cart created = new Cart(userId);
            List<Cart> all = carts.findAll();
            all.add(created);
            carts.saveAll(all);
            return created;
        });
    }

    public void addToCart(String userId, String productId, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be >= 1.");

        Product p = products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));
        if (!p.isAvailableForClient())
            throw new IllegalArgumentException("Product is not available for customers.");

        Cart cart = getOrCreateCart(userId);
        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
        if (existing == null) {
            cart.getItems().add(new CartItem(productId, p.getPrice(), qty));
        }
        else {
            existing.setQty(existing.getQty() + qty);
        }
        persistCart(cart);
    }

    public void removeFromCart(String userId, String productId) {
        Cart cart = getOrCreateCart(userId);
        boolean removed = cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        if (!removed) throw new IllegalArgumentException("Item not found in cart");
        persistCart(cart);
    }

    public void updateQty(String userId, String productId, int qty) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart."));

        if (qty < 0 ) throw new IllegalArgumentException("Quantity cannot be negative.");
        if (qty == 0) {
            cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        }
        else {
            item.setQty(qty);
        }
        persistCart(cart);
    }

    public double total(String userId) {
        Cart cart = getOrCreateCart(userId);
        return cart.getItems().stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQty())
                .sum();
    }

    public void clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        persistCart(cart);
    }

    public void persistCart(Cart cart) {
        List<Cart> all = carts.findAll();
        boolean replaced = false;
        for (int i=0; i<all.size(); i++) {
            if (all.get(i).getUserId().equals(cart.getUserId())) {
                all.set(i, cart);
                replaced = true;
                break;
            }
        }
        if (!replaced) { all.add(cart);}
        carts.saveAll(all);
    }
}