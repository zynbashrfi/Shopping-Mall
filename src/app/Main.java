package app;

import domain.User;
import persistence.JsonDB;
import persistence.JsonUserRepository;
import persistence.UserRepository;
import service.AuthService;
import util.PasswordUtil;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        String usersPath = "data/users.json";
        JsonDB db = new JsonDB();
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