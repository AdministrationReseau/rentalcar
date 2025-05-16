package inc.yowyob.rentalcar.domain.service;

import inc.yowyob.rentalcar.domain.model.User;
import java.util.Optional;

public interface AuthenticationService {
    User register(String username, String email, String name, String phoneNumber, String password, String role);
    Optional<User> login(String email, String password);
    void logout(String token);
    boolean validateToken(String token);
}
