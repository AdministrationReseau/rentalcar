package inc.yowyob.rentalcar.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de connexion utilisateur")
public class UserLoginRequest {

    @NotBlank(message = "Le username est obligatoire")
    @Schema(description = "Nom d'utilisateur ou email", example = "johndoe", required = true)
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Schema(description = "Mot de passe", example = "password123", required = true)
    private String password;
}
