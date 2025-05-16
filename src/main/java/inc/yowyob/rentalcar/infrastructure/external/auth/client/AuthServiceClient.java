package inc.yowyob.rentalcar.infrastructure.external.auth.client;

import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthLoginRequest;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthLoginResponse;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthRegisterRequest;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthServiceClient {

    private final WebClient webClient;

    @Autowired
    public AuthServiceClient(@Value("${external.api.url}") String baseUrl,
                             WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl(baseUrl)
            .build();
    }

    public Mono<ExternalAuthRegisterResponse> register(ExternalAuthRegisterRequest request) {
        return webClient.post()
            .uri("/auth-service/auth/register")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ExternalAuthRegisterResponse.class);
    }

    public Mono<ExternalAuthLoginResponse> login(ExternalAuthLoginRequest request) {
        return webClient.post()
            .uri("/auth-service/auth/login")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ExternalAuthLoginResponse.class);
    }

    public Mono<Void> logout(String token) {
        return webClient.post()
            .uri("/auth-service/auth/logout")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<Boolean> validateToken(String token) {
        return webClient.get()
            .uri("/auth-service/auth/validate-token")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(Boolean.class);
    }
}
