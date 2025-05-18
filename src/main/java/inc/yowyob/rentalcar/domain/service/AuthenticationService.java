package inc.yowyob.rentalcar.domain.service;

import inc.yowyob.rentalcar.domain.model.AuthenticationResult;
import inc.yowyob.rentalcar.domain.model.User;
import java.util.Optional;

public interface AuthenticationService {
    User register(String username, String email, String name, String phoneNumber, String password, String role);
    Optional<User> login(String username, String password);
    void logout(String token);
    boolean validateToken(String token);

    // Nouvelles méthodes qui retournent également le token
    AuthenticationResult registerWithToken(String username, String email, String name, String phoneNumber, String password, String role);
    Optional<AuthenticationResult> loginWithToken(String username, String password);
}
