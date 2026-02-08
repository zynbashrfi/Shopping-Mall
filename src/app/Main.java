package app;

import domain.User;
import persistence.JsonDB;
import persistence.JsonUserRepository;
import persistence.UserRepository;
import persistence.JsonProductRepository;
import persistence.ProductRepository;
import persistence.CartRepository;
import persistence.JsonCartRepository;
import service.*;
import ui.AppFrame;
import util.PasswordUtil;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String userPath = "data/users.json";
        String productsPath = "data/products.json";
        String cartsPath = "data/carts.json";
        JsonDB db = new JsonDB();

        UserRepository userRepo = new JsonUserRepository(userPath, db);
        ProductRepository productRepo = new JsonProductRepository(productsPath, db);
        CartRepository cartRepo = new JsonCartRepository(cartsPath, db);

        AuthService auth = new AuthService(userRepo);
        CatalogService catalog = new CatalogService(productRepo);
        CartService cart = new CartService(cartRepo, productRepo);
        CheckoutService checkout = new CheckoutService(cartRepo, productRepo);
        Session session = new Session();

        AppContext ctx = new AppContext(auth, catalog, cart, checkout, session);
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            AppFrame frame = new AppFrame(ctx);
            frame.setVisible(true);
        });

    }
}