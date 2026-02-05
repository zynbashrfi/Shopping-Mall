package persistence;

import domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findByUsername(String username);
    Optional<User> findById(String id);
    void saveAll(List<User> users);
}