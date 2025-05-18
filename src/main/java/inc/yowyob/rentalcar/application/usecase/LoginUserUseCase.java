package inc.yowyob.rentalcar.application.usecase;

import inc.yowyob.rentalcar.application.dto.AuthResponseDTO;
import inc.yowyob.rentalcar.application.dto.LoginUserDTO;
import inc.yowyob.rentalcar.application.mapper.UserMapper;
import inc.yowyob.rentalcar.domain.exception.AuthenticationException;
import inc.yowyob.rentalcar.domain.model.AuthenticationResult;
import inc.yowyob.rentalcar.domain.service.AuthenticationService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoginUserUseCase {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    public LoginUserUseCase(
        AuthenticationService authenticationService,
        UserMapper userMapper) {
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
    }

    public AuthResponseDTO execute(LoginUserDTO loginUserDTO) {
        try {
            // Utiliser la nouvelle méthode qui retourne aussi le token
            Optional<AuthenticationResult> resultOpt = authenticationService.loginWithToken(
                loginUserDTO.getUsername(),
                loginUserDTO.getPassword()
            );

            if (resultOpt.isEmpty()) {
                return AuthResponseDTO.builder()
                    .success(false)
                    .message("Email ou mot de passe incorrect")
                    .build();
            }

            AuthenticationResult result = resultOpt.get();

            return AuthResponseDTO.builder()
                .user(userMapper.toDTO(result.getUser()))
                .token(result.getToken())
                .success(true)
                .message("Authentification réussie")
                .build();

        } catch (AuthenticationException e) {
            return AuthResponseDTO.builder()
                .success(false)
                .message("Erreur d'authentification: " + e.getMessage())
                .build();
        } catch (Exception e) {
            return AuthResponseDTO.builder()
                .success(false)
                .message("Erreur lors de l'authentification: " + e.getMessage())
                .build();
        }
    }
}
