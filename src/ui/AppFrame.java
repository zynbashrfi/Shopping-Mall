package ui;

import domain.Role;
import domain.User;
import service.AppContext;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {
    private final AppContext ctx;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    public static final String LOGIN = "LOGIN";
    public static final String SIGNUP = "SIGNUP";
    public static final String CUSTOMER = "CUSTOMER";
    public static final String ADMIN = "ADMIN";

    public AppFrame(AppContext ctx) {
        super("Shopping Mall");
        this.ctx = ctx;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 500);
        setResizable(false);
        setLocationRelativeTo(null);

        root.add(new LoginPanel(ctx, this), LOGIN);
        root.add(new SignupPanel(ctx, this), SIGNUP);
        root.add(new CustomerPanel(ctx, this), CUSTOMER);
        root.add(new AdminPanel(ctx, this), ADMIN);

        setContentPane(root);
        showLogin();
    }

    public void showLogin() { cards.show(root, LOGIN); }
    public void showSignup() { cards.show(root, SIGNUP); }
    public void routeAfterLogin(User user) {
        if (user.getRole() == Role.ADMIN) cards.show(root, ADMIN);
        else cards.show(root, CUSTOMER);
    }
}