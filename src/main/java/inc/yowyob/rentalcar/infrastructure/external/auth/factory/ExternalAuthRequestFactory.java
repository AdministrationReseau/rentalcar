package inc.yowyob.rentalcar.infrastructure.external.auth.factory;

import inc.yowyob.rentalcar.domain.model.User;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthLoginRequest;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthRegisterRequest;
import org.springframework.stereotype.Component;

@Component
public class ExternalAuthRequestFactory {

    public ExternalAuthRegisterRequest createRegisterRequest(User user) {
        return ExternalAuthRegisterRequest.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .password(user.getPassword())
            .name(user.getName())
            .phoneNumber(user.getPhoneNumber())
            .build();
    }

    public ExternalAuthLoginRequest createLoginRequest(String username, String password) {
        return ExternalAuthLoginRequest.builder()
            .username(username)
            .password(password)
            .build();
    }
}
