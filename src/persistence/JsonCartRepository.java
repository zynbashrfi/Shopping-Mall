package persistence;

import domain.Cart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JsonCartRepository implements CartRepository {
    private final String path;
    private final JsonDB db;

    public JsonCartRepository(String path, JsonDB db) {
        this.path = path;
        this.db = db;
    }

    @Override
    public List<Cart> findAll() {
        Cart[] arr = db.read(path, Cart[].class, new Cart[0]);
        return new ArrayList<>(Arrays.asList(arr));
    }

    @Override
    public Optional<Cart> findByUserId(String userId) {
        return findAll().stream()
                .filter(c -> c.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void saveAll(List<Cart> carts) {
        db.write(path, carts);
    }
}