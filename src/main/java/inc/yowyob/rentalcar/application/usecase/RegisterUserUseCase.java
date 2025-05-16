package inc.yowyob.rentalcar.application.usecase;

import inc.yowyob.rentalcar.application.dto.AuthResponseDTO;
import inc.yowyob.rentalcar.application.dto.RegisterUserDTO;
import inc.yowyob.rentalcar.application.mapper.UserMapper;
import inc.yowyob.rentalcar.domain.exception.AuthenticationException;
import inc.yowyob.rentalcar.domain.exception.UserAlreadyExistsException;
import inc.yowyob.rentalcar.domain.model.User;
import inc.yowyob.rentalcar.domain.service.AuthenticationService;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserUseCase {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    public RegisterUserUseCase(
        AuthenticationService authenticationService,
        UserMapper userMapper) {
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
    }

    public AuthResponseDTO execute(RegisterUserDTO registerUserDTO) {
        try {
            User registeredUser = authenticationService.register(
                registerUserDTO.getUsername(),
                registerUserDTO.getEmail(),
                registerUserDTO.getName(),
                registerUserDTO.getPhoneNumber(),
                registerUserDTO.getPassword(),
                registerUserDTO.getRole()
            );

            return AuthResponseDTO.builder()
                .user(userMapper.toDTO(registeredUser))
                .success(true)
                .message("Utilisateur enregistré avec succès")
                .build();

        } catch (UserAlreadyExistsException e) {
            return AuthResponseDTO.builder()
                .success(false)
                .message(e.getMessage())
                .build();
        } catch (AuthenticationException e) {
            return AuthResponseDTO.builder()
                .success(false)
                .message("Erreur d'authentification: " + e.getMessage())
                .build();
        } catch (Exception e) {
            return AuthResponseDTO.builder()
                .success(false)
                .message("Erreur lors de l'enregistrement: " + e.getMessage())
                .build();
        }
    }
}
