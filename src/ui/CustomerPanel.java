package ui;

import service.AppContext;

import javax.swing.*;
import java.awt.*;

public class CustomerPanel extends JPanel {
    public CustomerPanel(AppContext ctx, AppFrame app) {
        setLayout(new BorderLayout());
        add(new JLabel("Customer Panel (TODO)"), BorderLayout.CENTER);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            ctx.session.logout();
            app.showLogin();
        });
        add(logout, BorderLayout.SOUTH);
    }
}