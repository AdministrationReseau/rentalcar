package inc.yowyob.rentalcar.application.service;

import inc.yowyob.rentalcar.domain.exception.AuthenticationException;
import inc.yowyob.rentalcar.domain.exception.UserAlreadyExistsException;
import inc.yowyob.rentalcar.domain.model.User;
import inc.yowyob.rentalcar.domain.repository.UserRepository;
import inc.yowyob.rentalcar.domain.service.AuthenticationService;
import inc.yowyob.rentalcar.infrastructure.external.auth.client.AuthServiceClient;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthLoginResponse;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthRegisterResponse;
import inc.yowyob.rentalcar.infrastructure.external.auth.factory.ExternalAuthRequestFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthServiceClient authServiceClient;
    private final ExternalAuthRequestFactory authRequestFactory;

    @Autowired
    public AuthenticationServiceImpl(
        UserRepository userRepository,
        AuthServiceClient authServiceClient,
        ExternalAuthRequestFactory authRequestFactory) {
        this.userRepository = userRepository;
        this.authServiceClient = authServiceClient;
        this.authRequestFactory = authRequestFactory;
    }

    @Override
    public User register(String username, String email, String name, String phoneNumber, String password, String role) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Un utilisateur avec cet email existe déjà: " + email);
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Un utilisateur avec ce nom d'utilisateur existe déjà: " + username);
        }

        // Créer l'utilisateur dans le domaine
        User newUser = User.builder()
            .id(UUID.randomUUID())
            .username(username)
            .email(email)
            .name(name)
            .phoneNumber(phoneNumber)
            .password(password)
            .role(role)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .active(true)
            .build();

        // Appeler l'API externe pour enregistrer l'utilisateur
        ExternalAuthRegisterResponse externalResponse = authServiceClient
            .register(authRequestFactory.createRegisterRequest(newUser))
            .block(); // Appel synchrone

        if (externalResponse == null || !externalResponse.isSuccess()) {
            throw new AuthenticationException("Échec de l'enregistrement dans le service externe: " +
                (externalResponse != null ? externalResponse.getMessage() : "Réponse nulle"));
        }

        // Mettre à jour l'utilisateur avec l'ID externe
        newUser.setExternalId(externalResponse.getId());

        // Sauvegarder l'utilisateur dans notre base de données
        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> login(String email, String password) {
        // Vérifier si l'utilisateur existe dans notre base de données
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        // Appeler l'API externe pour authentifier l'utilisateur
        ExternalAuthLoginResponse externalResponse = authServiceClient
            .login(authRequestFactory.createLoginRequest(email, password))
            .block(); // Appel synchrone

        if (externalResponse == null || !externalResponse.isSuccess()) {
            throw new AuthenticationException("Échec de l'authentification dans le service externe: " +
                (externalResponse != null ? externalResponse.getMessage() : "Réponse nulle"));
        }

        return optionalUser;
    }

    @Override
    public void logout(String token) {
        authServiceClient.logout(token).block(); // Appel synchrone
    }

    @Override
    public boolean validateToken(String token) {
        return authServiceClient.validateToken(token).block(); // Appel synchrone
    }
}
