package inc.yowyob.rentalcar.presentation.controller;

import inc.yowyob.rentalcar.application.dto.AuthResponseDTO;
import inc.yowyob.rentalcar.application.usecase.LoginUserUseCase;
import inc.yowyob.rentalcar.application.usecase.RegisterUserUseCase;
import inc.yowyob.rentalcar.presentation.dto.AuthenticationResponse;
import inc.yowyob.rentalcar.presentation.dto.UserLoginRequest;
import inc.yowyob.rentalcar.presentation.dto.UserRegistrationRequest;
import inc.yowyob.rentalcar.presentation.mapper.AuthMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentification", description = "API d'authentification des utilisateurs")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final AuthMapper authMapper;

    public AuthController(
        RegisterUserUseCase registerUserUseCase,
        LoginUserUseCase loginUserUseCase,
        AuthMapper authMapper) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.authMapper = authMapper;
    }

    @Operation(
        summary = "Enregistrer un nouvel utilisateur",
        description = "Crée un nouveau compte utilisateur dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Utilisateur enregistré avec succès",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuthenticationResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données d'inscription invalides ou utilisateur déjà existant",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuthenticationResponse.class)
            )
        )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        AuthResponseDTO responseDTO = registerUserUseCase.execute(authMapper.toRegisterUserDTO(request));

        HttpStatus status = responseDTO.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(authMapper.toAuthenticationResponse(responseDTO));
    }

    @Operation(
        summary = "Connexion utilisateur",
        description = "Authentifie un utilisateur et retourne un token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Authentification réussie",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuthenticationResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Identifiants invalides",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuthenticationResponse.class)
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody UserLoginRequest request) {
        AuthResponseDTO responseDTO = loginUserUseCase.execute(authMapper.toLoginUserDTO(request));

        HttpStatus status = responseDTO.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(authMapper.toAuthenticationResponse(responseDTO));
    }
}
