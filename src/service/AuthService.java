package service;

import domain.User;
import domain.Role;
import persistence.UserRepository;
import util.IdUtil;
import util.PasswordUtil;
import util.Validators;

import java.util.List;

public class AuthService {

    private final UserRepository users;

    public AuthService(UserRepository users) {
        this.users = users;
    }
    public User login(String username, String passwordPlain) {
        Validators.requireNonBlank(username, "Username");
        Validators.requireNonBlank(passwordPlain, "Password");

        User user = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        String hash = PasswordUtil.sha25Hex(passwordPlain);
        if (!user.getPasswordHash().equals(hash))
            throw new IllegalArgumentException("Wrong password");

        return user;
    }

    public User signup(String username, String passwordPlain) {

        Validators.requireNonBlank(username, "Username");
        Validators.requireMinLen(username.trim(), 3, "Username");

        Validators.requireNonBlank(passwordPlain, "Password");
        Validators.requireMinLen(passwordPlain, 4, "Password");

        if (users.findByUsername(username).isPresent())
            throw new IllegalArgumentException("Username already exists.");

        List<User> all = users.findAll();
        User created = new User(
                IdUtil.newsUserId(),
                username.trim(),
                PasswordUtil.sha25Hex(passwordPlain),
                Role.CUSTOMER
        );
        all.add(created);
        users.saveAll(all);
        return created;
    }

}