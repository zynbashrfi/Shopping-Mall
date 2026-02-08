package ui;

import service.AppContext;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    public AdminPanel(AppContext ctx, AppFrame app) {
        setLayout(new BorderLayout());
        add(new JLabel("Admin Panel (TODO)"), BorderLayout.CENTER);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            ctx.session.logout();
            app.showLogin();
        });
        add(logout, BorderLayout.SOUTH);
    }
}