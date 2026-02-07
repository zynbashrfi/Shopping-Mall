package persistence;

import domain.Cart;

import java.util.List;
import java.util.Optional;

public interface CartRepository {
    List<Cart> findAll();
    Optional<Cart> findByUserId(String userId);
    void saveAll(List<Cart> carts);
}