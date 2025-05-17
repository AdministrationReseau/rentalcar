package inc.yowyob.rentalcar.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.yowyob.rentalcar.presentation.dto.AuthenticationResponse;
import inc.yowyob.rentalcar.presentation.dto.UserLoginRequest;
import inc.yowyob.rentalcar.presentation.dto.UserRegistrationRequest;
import inc.yowyob.rentalcar.presentation.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/proxy/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Proxy Authentification", description = "Proxy pour contourner les restrictions CORS du service d'authentification externe")
public class AuthProxyController {

    private final WebClient webClient;

    public AuthProxyController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://gateway.yowyob.com")
            .build();
    }

    @Operation(
        summary = "Proxy pour l'enregistrement d'un nouvel utilisateur",
        description = "Transfère la requête d'enregistrement au service externe"
    )
    @PostMapping("/register")
    public Mono<ResponseEntity<Object>> register(@Valid @RequestBody UserRegistrationRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonBody = mapper.writeValueAsString(request);
            System.out.println("Corps de la requête: " + jsonBody);

            return webClient.post()
                .uri("/auth-service/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                // Ajoutez ici d'autres headers si nécessaires
                // .header("Authorization", "Bearer token")
                .bodyValue(jsonBody)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnNext(response -> System.out.println("Réponse: " + response))
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(e -> {
                    System.err.println("Erreur: " + e.getMessage());
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException wcre = (WebClientResponseException) e;
                        System.err.println("Corps de l'erreur: " + wcre.getResponseBodyAsString());
                        return Mono.just(ResponseEntity.status(wcre.getStatusCode())
                            .body(wcre.getResponseBodyAsString()));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur de communication: " + e.getMessage()));
                });
        } catch (JsonProcessingException e) {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur de sérialisation: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Proxy pour la connexion utilisateur",
        description = "Transfère la requête de connexion au service externe"
    )
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthenticationResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        return webClient.post()
            .uri("/auth-service/auth/login")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ExternalAuthResponse.class)
            .map(externalResponse -> {
                // Convertir la réponse externe en AuthenticationResponse
                AuthenticationResponse response = mapToAuthenticationResponse(externalResponse);

                // Déterminer le statut HTTP en fonction du succès de l'opération
                HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;

                return ResponseEntity.status(status).body(response);
            })
            .onErrorResume(e -> {
                // En cas d'erreur, retourner une réponse d'échec
                AuthenticationResponse errorResponse = AuthenticationResponse.builder()
                    .success(false)
                    .message("Erreur lors de la communication avec le service externe: " + e.getMessage())
                    .build();

                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
            });
    }

    // Classe pour la réponse du service externe
    private static class ExternalAuthResponse {
        private String id;
        private String username;
        private String email;
        private String token;
        private boolean success;
        private String message;
        private String name;
        private String phoneNumber;
        private String role;

        // Getters et setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    // Méthode pour mapper la réponse externe vers AuthenticationResponse
    private AuthenticationResponse mapToAuthenticationResponse(ExternalAuthResponse externalResponse) {
        UserResponse userResponse = null;

        if (externalResponse.isSuccess()) {
            // Créer un UserResponse seulement si la réponse est un succès
            userResponse = UserResponse.builder()
                .id(externalResponse.getId() != null ? UUID.fromString(externalResponse.getId()) : null)
                .username(externalResponse.getUsername())
                .email(externalResponse.getEmail())
                .name(externalResponse.getName())
                .phoneNumber(externalResponse.getPhoneNumber())
                .role(externalResponse.getRole())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        }

        return AuthenticationResponse.builder()
            .user(userResponse)
            .token(externalResponse.getToken())
            .success(externalResponse.isSuccess())
            .message(externalResponse.getMessage())
            .build();
    }
}
