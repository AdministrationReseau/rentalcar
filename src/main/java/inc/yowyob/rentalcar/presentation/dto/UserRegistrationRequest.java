package inc.yowyob.rentalcar.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'inscription d'un nouvel utilisateur")
public class UserRegistrationRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Schema(description = "Nom d'utilisateur unique", example = "johndoe", required = true)
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Schema(description = "Adresse email", example = "john.doe@example.com", required = true)
    private String email;

    @NotBlank(message = "Le nom est obligatoire")
    @Schema(description = "Nom complet", example = "John Doe", required = true)
    private String name;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Schema(description = "Numéro de téléphone", example = "+237675473829", required = true)
    private String phoneNumber;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Schema(description = "Mot de passe (minimum 6 caractères)", example = "password123", required = true)
    private String password;

    @NotBlank(message = "Le rôle est obligatoire")
    @Schema(description = "Rôle de l'utilisateur", example = "customer", allowableValues = {"customer", "agency_owner", "staff"}, required = true)
    private String role;
}
