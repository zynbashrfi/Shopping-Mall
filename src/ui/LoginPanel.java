package ui;

import domain.User;
import service.AppContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {

  private static final Color HEADER_GREEN = new Color(0, 220, 0);

  public LoginPanel(AppContext ctx, AppFrame app) {
    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(30, 30, 30, 30));

    JPanel centerWrap = new JPanel(new GridBagLayout());
    centerWrap.setOpaque(false);

    JPanel card = new JPanel(new BorderLayout(0, 18));
    card.setPreferredSize(new Dimension(720, 360)); // کنترل حس صفحه مثل PDF
    card.setBorder(new EmptyBorder(20, 30, 20, 30));

    JLabel header = new JLabel("Please enter your username and password", SwingConstants.CENTER);
    header.setOpaque(true);
    header.setBackground(HEADER_GREEN);
    header.setForeground(Color.BLACK);
    header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
    header.setBorder(new EmptyBorder(12, 10, 12, 10));
    card.add(header, BorderLayout.NORTH);

    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);

    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();

    Dimension fieldSize = new Dimension(320, 30);
    usernameField.setPreferredSize(fieldSize);
    passwordField.setPreferredSize(fieldSize);

    GridBagConstraints gc = new GridBagConstraints();
    gc.insets = new Insets(10, 10, 10, 10);
    gc.fill = GridBagConstraints.NONE;

    gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.EAST;
    form.add(new JLabel("Username"), gc);

    gc.gridx = 1; gc.gridy = 0; gc.anchor = GridBagConstraints.WEST;
    form.add(usernameField, gc);

    gc.gridx = 0; gc.gridy = 1; gc.anchor = GridBagConstraints.EAST;
    form.add(new JLabel("Password"), gc);

    gc.gridx = 1; gc.gridy = 1; gc.anchor = GridBagConstraints.WEST;
    form.add(passwordField, gc);

    card.add(form, BorderLayout.CENTER);

    JPanel buttons = new JPanel(new GridBagLayout());
    buttons.setOpaque(false);

    JButton signUpBtn = new JButton("Sign up");
    JButton signInBtn = new JButton("Sign in");

    signUpBtn.setPreferredSize(new Dimension(120, 34));
    signInBtn.setPreferredSize(new Dimension(280, 34));

    GridBagConstraints bc = new GridBagConstraints();
    bc.insets = new Insets(0, 10, 0, 10);
    bc.gridy = 0;

    bc.gridx = 0; bc.anchor = GridBagConstraints.WEST;
    buttons.add(signUpBtn, bc);

    bc.gridx = 1; bc.anchor = GridBagConstraints.EAST;
    buttons.add(signInBtn, bc);

    card.add(buttons, BorderLayout.SOUTH);

    centerWrap.add(card);
    add(centerWrap, BorderLayout.CENTER);

    signInBtn.addActionListener(e -> {
      try {
        User user = ctx.auth.login(usernameField.getText(), new String(passwordField.getPassword()));
        ctx.session.login(user);
        passwordField.setText("");
        app.routeAfterLogin(user);
      } catch (Exception ex) {
        UtilDialogs.error(this, ex.getMessage());
      }
    });

    signUpBtn.addActionListener(e -> app.showSignup());
  }
}
