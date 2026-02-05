package persistence;

import domain.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JsonProductRepository implements ProductRepository {
    private final String path;
    private final JsonDB db;

    public JsonProductRepository(String path, JsonDB db) {
        this.path = path;
        this.db = db;
    }

    @Override
    public List<Product> findAll() {
        Product[] arr = db.read(path, Product[].class, new Product[0]);
        return new ArrayList<>(Arrays.asList(arr));
    }

    @Override
    public Optional<Product> findById(String id) {
        return findAll().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public void saveAll(List<Product> products) {
        db.write(path, products);
    }
}