package service;

import domain.User;

public class Session {
    private User current;

    public void login(User u) { this.current = u; }
    public void logout() { this.current = null; }

    public User requireUser() {
        if (current == null ) throw new IllegalArgumentException("Not logged in.");
        return current;
    }

    public User getCurrent() { return current; }
}