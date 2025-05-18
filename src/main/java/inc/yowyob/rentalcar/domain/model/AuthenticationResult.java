package inc.yowyob.rentalcar.domain.model;

import lombok.Getter;

@Getter
public class AuthenticationResult {
    private final User user;
    private final String token;

    public AuthenticationResult(User user, String token) {
        this.user = user;
        this.token = token;
    }
}
