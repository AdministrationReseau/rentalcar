package inc.yowyob.rentalcar.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse d'authentification contenant les informations utilisateur et le token")
public class AuthenticationResponse {

    @Schema(description = "Informations de l'utilisateur authentifié")
    private UserResponse user;

    @Schema(description = "Token JWT pour authentifier les requêtes ultérieures", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Indique si l'opération d'authentification a réussi", example = "true")
    private boolean success;

    @Schema(description = "Message explicatif (succès ou erreur)", example = "Authentification réussie")
    private String message;
}
