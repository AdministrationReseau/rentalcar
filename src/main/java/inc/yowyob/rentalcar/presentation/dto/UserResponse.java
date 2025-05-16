package inc.yowyob.rentalcar.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations d'un utilisateur")
public class UserResponse {

    @Schema(description = "Identifiant unique de l'utilisateur", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Nom d'utilisateur", example = "johndoe")
    private String username;

    @Schema(description = "Adresse email", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Nom complet", example = "John Doe")
    private String name;

    @Schema(description = "Numéro de téléphone", example = "+237675473829")
    private String phoneNumber;

    @Schema(description = "Rôle de l'utilisateur", example = "customer", allowableValues = {"customer", "agency_owner", "admin"})
    private String role;

    @Schema(description = "Date de création du compte", example = "2025-05-15T15:30:45")
    private LocalDateTime createdAt;

    @Schema(description = "Indique si le compte est actif", example = "true")
    private boolean active;
}
