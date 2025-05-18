package inc.yowyob.rentalcar.infrastructure.external.auth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthLoginRequest;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthLoginResponse;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthRegisterRequest;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthServiceClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthServiceClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
            .baseUrl("http://localhost:8080")
            .build();
        this.objectMapper = objectMapper;
    }

    public Mono<ExternalAuthRegisterResponse> register(ExternalAuthRegisterRequest request) {
        System.out.println("AuthServiceClient: Envoi de la requête au proxy: " + request);

        return webClient.post()
            .uri("/api/proxy/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(responseStr -> {
                System.out.println("AuthServiceClient: Réponse brute du proxy: " + responseStr);

                try {
                    // Analyser la réponse JSON
                    JsonNode jsonNode = objectMapper.readTree(responseStr);

                    // Créer et remplir l'objet de réponse
                    ExternalAuthRegisterResponse response = new ExternalAuthRegisterResponse();

                    if (jsonNode.has("id")) response.setId(jsonNode.get("id").asText());
                    if (jsonNode.has("username")) response.setUsername(jsonNode.get("username").asText());
                    if (jsonNode.has("email")) response.setEmail(jsonNode.get("email").asText());
                    if (jsonNode.has("name")) response.setName(jsonNode.get("name").asText());
                    if (jsonNode.has("phone_number")) response.setPhone_number(jsonNode.get("phone_number").asText());
                    if (jsonNode.has("active")) response.setActive(jsonNode.get("active").asBoolean());

                    // Considérer comme succès si un ID est présent
                    boolean success = jsonNode.has("id") && !jsonNode.get("id").isNull();
                    response.setSuccess(success);
                    response.setMessage(success ? "Enregistrement réussi" : "Échec de l'enregistrement");

                    return Mono.just(response);
                } catch (Exception e) {
                    System.err.println("AuthServiceClient: Erreur lors de l'analyse de la réponse: " + e.getMessage());
                    return Mono.error(e);
                }
            })
            .doOnError(e -> {
                System.err.println("AuthServiceClient: Erreur lors de l'enregistrement: " + e.getMessage());
                e.printStackTrace();
            });
    }

    public Mono<ExternalAuthLoginResponse> login(ExternalAuthLoginRequest request) {
        System.out.println("AuthServiceClient: Envoi de la requête login au proxy: " + request);

        return webClient.post()
            .uri("/api/proxy/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(responseStr -> {
                System.out.println("AuthServiceClient: Réponse brute du proxy login: " + responseStr);

                try {
                    // Analyser la réponse JSON
                    JsonNode jsonNode = objectMapper.readTree(responseStr);

                    // Créer et remplir l'objet de réponse
                    ExternalAuthLoginResponse response = new ExternalAuthLoginResponse();

                    if (jsonNode.has("id")) response.setId(jsonNode.get("id").asText());
                    if (jsonNode.has("username")) response.setUsername(jsonNode.get("username").asText());
                    if (jsonNode.has("email")) response.setEmail(jsonNode.get("email").asText());
                    if (jsonNode.has("token")) response.setToken(jsonNode.get("token").asText());

                    // Considérer comme succès si un token est présent
                    boolean success = jsonNode.has("token") && !jsonNode.get("token").isNull();
                    response.setSuccess(success);
                    response.setMessage(success ? "Authentification réussie" : "Échec de l'authentification");

                    return Mono.just(response);
                } catch (Exception e) {
                    System.err.println("AuthServiceClient: Erreur lors de l'analyse de la réponse login: " + e.getMessage());
                    return Mono.error(e);
                }
            })
            .doOnError(e -> {
                System.err.println("AuthServiceClient: Erreur lors du login: " + e.getMessage());
                e.printStackTrace();
            });
    }

    public Mono<Void> logout(String token) {
        return webClient.post()
            .uri("/api/proxy/auth/logout")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnError(e -> {
                System.err.println("AuthServiceClient: Erreur lors de la déconnexion: " + e.getMessage());
            });
    }

    public Mono<Boolean> validateToken(String token) {
        return webClient.get()
            .uri("/api/proxy/auth/validate-token")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(Boolean.class)
            .doOnError(e -> {
                System.err.println("AuthServiceClient: Erreur lors de la validation du token: " + e.getMessage());
            });
    }
}
