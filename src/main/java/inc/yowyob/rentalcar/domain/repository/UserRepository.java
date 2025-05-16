package inc.yowyob.rentalcar.domain.repository;

import inc.yowyob.rentalcar.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    List<User> findByRole(String role);
    void delete(UUID id);
}
