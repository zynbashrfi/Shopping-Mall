package app;

import domain.User;
import persistence.JsonDB;
import persistence.JsonUserRepository;
import persistence.UserRepository;
import persistence.JsonProductRepository;
import persistence.ProductRepository;
import persistence.CartRepository;
import persistence.JsonCartRepository;
import service.CatalogService;
import service.AuthService;
import service.CartService;
import service.CheckoutService;
import util.PasswordUtil;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        JsonDB db = new JsonDB();

        // product tests:
        String productsPath = "data/products.json";
        ProductRepository productRepo = new JsonProductRepository(productsPath, db);
        CatalogService catalog = new CatalogService(productRepo);

        System.out.println("All products: " + catalog.listAll().size());
        System.out.println("Customer products: " + catalog.listForCustomer());

        System.out.println("After add: " + catalog.listAll().size());

        // auth tests:
        String usersPath = "data/users.json";
        UserRepository userRepo = new JsonUserRepository(usersPath, db);
        AuthService auth = new AuthService(userRepo);

        List<User> users = userRepo.findAll();
        boolean updated = false;
        for (int i=0; i<users.size(); i++) {
            User u = users.get(i);
            if ("admin".equalsIgnoreCase(u.getUsername()) && "__REPLACE__".equals(u.getPasswordHash())) {
                users.set(i, new domain.User(u.getId(), u.getUsername(), PasswordUtil.sha25Hex("admin"), u.getRole()));
                updated = true;
            }
        }

        if (updated) userRepo.saveAll(users);

        // login admin test:
        System.out.println("Try admin login...");
        System.out.println(auth.login("admin", "admin").getRole());

        // signup + login test:
        String uname = "testuser";
        try {
            auth.signup(uname, "1234");
            System.out.println("Signed up: " + uname);
        } catch (Exception e) {
            System.out.println("Signup skipped: " + e.getMessage());
        }

        System.out.println("login testuser...");
        System.out.println(auth.login(uname, "1234").getRole());

        System.out.println("Done.");

        // cart tests:
        String cartPath = "data/carts.json";

        CartRepository cartRepo = new JsonCartRepository(cartPath, db);
        CartService cartService = new CartService(cartRepo, productRepo);

        User test = auth.login("testuser", "1234");

        System.out.println("Cart total initially: " + cartService.total(test.getId()));
        String firstProductId = catalog.listForCustomer().get(0).getId();
        cartService.addToCart(test.getId(), firstProductId, 2);
        System.out.println("Cart total after add: " + cartService.total(test.getId()));
        System.out.println("Cart after add: " + cartService.getOrCreateCart(test.getId()));

        cartService.updateQty(test.getId(), firstProductId, 1);
        System.out.println("Cart after update: " + cartService.getOrCreateCart(test.getId()));

        cartService.removeFromCart(test.getId(), firstProductId);
        System.out.println("Cart after remove: " + cartService.getOrCreateCart(test.getId()));

        // checkout tests:
        CheckoutService checkoutService = new CheckoutService(cartRepo, productRepo);
        String pid = catalog.listForCustomer().get(0).getId();
        cartService.addToCart(test.getId(), pid, 2);
        System.out.println("Before checkout cart: " + cartService.getOrCreateCart(test.getId()));
        System.out.println("Stock before: " + productRepo.findById(pid).get().getStock());

        checkoutService.checkout(test.getId());

        System.out.println("After checkout cart: " + cartService.getOrCreateCart(test.getId()));
        System.out.println("Stock after: " + productRepo.findById(pid).get().getStock());

        try {
            cartService.addToCart(test.getId(), pid, 9999);
            checkoutService.checkout(test.getId());
        } catch (Exception e) {
            System.out.println("Expected checkout error: " + e.getMessage());
        }
    }
}