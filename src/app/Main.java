package app;

import domain.User;
import persistence.JsonDB;
import persistence.JsonUserRepository;
import persistence.UserRepository;
import persistence.JsonProductRepository;
import persistence.ProductRepository;
import service.CatalogService;
import service.AuthService;
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

        catalog.addProduct("Mouse", 15.0, "Electronics", 50, true);
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
    }
}