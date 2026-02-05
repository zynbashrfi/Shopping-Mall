package persistence;

import domain.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JsonUserRepository implements UserRepository {
    private final String path;
    private final JsonDB db;

    public JsonUserRepository(String path, JsonDB db) {
        this.path = path;
        this.db = db;
    }

    @Override
    public List<User> findAll() {
        User[] arr = db.read(path, User[].class, new User[0]);
        return new ArrayList<>(Arrays.asList(arr));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public Optional<User> findById(String id) {
        return findAll().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public void saveAll(List<User> users) {
        db.write(path, users);
    }
}