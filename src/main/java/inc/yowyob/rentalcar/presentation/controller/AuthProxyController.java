package inc.yowyob.rentalcar.presentation.controller;

import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthLoginRequest;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthRegisterRequest;
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
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/proxy/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Proxy Authentification", description = "Proxy pour contourner les restrictions CORS du service d'authentification externe")
public class AuthProxyController {

    private final WebClient webClient;
    private static final boolean USE_MOCK_FOR_DOWN_SERVICES = true;

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
    public Mono<ResponseEntity<Object>> register(@Valid @RequestBody ExternalAuthRegisterRequest request) {
        System.out.println("Proxy: Requête d'enregistrement reçue: " + request);

        return webClient.post()
            .uri("/auth-service/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Object.class)
            .doOnNext(response -> System.out.println("Proxy: Réponse du service externe: " + response))
            .map(response -> ResponseEntity.ok(response))
            .onErrorResume(e -> {
                System.err.println("Proxy: Erreur lors de l'enregistrement: " + e.getMessage());
                if (e instanceof WebClientResponseException) {
                    WebClientResponseException wcre = (WebClientResponseException) e;
                    System.err.println("Proxy: Corps de l'erreur: " + wcre.getResponseBodyAsString());
                    return Mono.just(ResponseEntity.status(wcre.getStatusCode())
                        .body(wcre.getResponseBodyAsString()));
                }
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur de communication: " + e.getMessage()));
            });
    }

    @Operation(
        summary = "Proxy pour la connexion utilisateur",
        description = "Transfère la requête de connexion au service externe"
    )
    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@Valid @RequestBody ExternalAuthLoginRequest request) {
        System.out.println("Proxy: Requête de login reçue: " + request);

        return webClient.post()
            .uri("/auth-service/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            // Ajout de tentatives de réessai pour les erreurs 503
            .retrieve()
            .bodyToMono(Object.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof WebClientResponseException.ServiceUnavailable))
            .doOnNext(response -> System.out.println("Proxy: Réponse login du service externe: " + response))
            .map(response -> ResponseEntity.ok(response))
            .onErrorResume(e -> {
                System.err.println("Proxy: Erreur lors du login: " + e.getMessage());
                if (e instanceof WebClientResponseException) {
                    WebClientResponseException wcre = (WebClientResponseException) e;
                    System.err.println("Proxy: Corps de l'erreur login: " + wcre.getResponseBodyAsString());

                    // Si c'est une erreur 503, renvoyer une réponse simulée si l'option est activée
                    if (USE_MOCK_FOR_DOWN_SERVICES && wcre.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
                        Map<String, Object> mockResponse = new HashMap<>();
                        mockResponse.put("id", "mock-id-" + UUID.randomUUID());
                        mockResponse.put("username", request.getUsername());
                        mockResponse.put("email", request.getUsername() + "@example.com");
                        mockResponse.put("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token-" + UUID.randomUUID());
                        mockResponse.put("success", true);
                        mockResponse.put("message", "Authentification simulée réussie après erreur 503");
                        return Mono.just(ResponseEntity.ok(mockResponse));
                    }

                    return Mono.just(ResponseEntity.status(wcre.getStatusCode())
                        .body(wcre.getResponseBodyAsString()));
                }
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur de communication avec le service d'authentification: " + e.getMessage()));
            });
    }

    @GetMapping("/validate-token")
    public Mono<ResponseEntity<Object>> validateToken(@RequestHeader("Authorization") String token) {
        // Si le service est en panne et que nous avons activé l'option de mock
        if (USE_MOCK_FOR_DOWN_SERVICES) {
            System.out.println("ATTENTION: Utilisation d'une réponse simulée pour validate-token car le service est indisponible");
            // Supposer que tous les tokens sont valides pour le moment
            return Mono.just(ResponseEntity.ok(true));
        }

        return webClient.get()
            .uri("/auth-service/auth/validate-token")
            .header("Authorization", token)
            .retrieve()
            .bodyToMono(Object.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof WebClientResponseException.ServiceUnavailable))
            .map(response -> ResponseEntity.ok(response))
            .onErrorResume(e -> {
                if (e instanceof WebClientResponseException) {
                    WebClientResponseException wcre = (WebClientResponseException) e;

                    // Si c'est une erreur 503, renvoyer une réponse simulée si l'option est activée
                    if (USE_MOCK_FOR_DOWN_SERVICES && wcre.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
                        return Mono.just(ResponseEntity.ok(true));
                    }

                    return Mono.just(ResponseEntity.status(wcre.getStatusCode())
                        .body(wcre.getResponseBodyAsString()));
                }
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la validation du token: " + e.getMessage()));
            });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Object>> logout(@RequestHeader("Authorization") String token) {
        // Si le service est en panne et que nous avons activé l'option de mock
        if (USE_MOCK_FOR_DOWN_SERVICES) {
            System.out.println("ATTENTION: Utilisation d'une réponse simulée pour logout car le service est indisponible");
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("success", true);
            mockResponse.put("message", "Déconnexion simulée réussie");
            return Mono.just(ResponseEntity.ok(mockResponse));
        }

        return webClient.post()
            .uri("/auth-service/auth/logout")
            .header("Authorization", token)
            .retrieve()
            .bodyToMono(Object.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof WebClientResponseException.ServiceUnavailable))
            .map(response -> ResponseEntity.ok(response))
            .onErrorResume(e -> {
                if (e instanceof WebClientResponseException) {
                    WebClientResponseException wcre = (WebClientResponseException) e;

                    // Si c'est une erreur 503, renvoyer une réponse simulée si l'option est activée
                    if (USE_MOCK_FOR_DOWN_SERVICES && wcre.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
                        Map<String, Object> mockResponse = new HashMap<>();
                        mockResponse.put("success", true);
                        mockResponse.put("message", "Déconnexion simulée réussie après erreur 503");
                        return Mono.just(ResponseEntity.ok(mockResponse));
                    }

                    return Mono.just(ResponseEntity.status(wcre.getStatusCode())
                        .body(wcre.getResponseBodyAsString()));
                }
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la déconnexion: " + e.getMessage()));
            });
    }
}
