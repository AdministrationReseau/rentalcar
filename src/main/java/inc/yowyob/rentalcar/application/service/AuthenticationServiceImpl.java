package inc.yowyob.rentalcar.application.service;

import inc.yowyob.rentalcar.domain.exception.AuthenticationException;
import inc.yowyob.rentalcar.domain.exception.UserAlreadyExistsException;
import inc.yowyob.rentalcar.domain.model.AuthenticationResult;
import inc.yowyob.rentalcar.domain.model.User;
import inc.yowyob.rentalcar.domain.repository.UserRepository;
import inc.yowyob.rentalcar.domain.service.AuthenticationService;
import inc.yowyob.rentalcar.infrastructure.external.auth.client.AuthServiceClient;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthLoginResponse;
import inc.yowyob.rentalcar.infrastructure.external.auth.dto.ExternalAuthRegisterResponse;
import inc.yowyob.rentalcar.infrastructure.external.auth.factory.ExternalAuthRequestFactory;
import inc.yowyob.rentalcar.infrastructure.security.BCryptPasswordEncoder;

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
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationServiceImpl(
        UserRepository userRepository,
        AuthServiceClient authServiceClient,
        ExternalAuthRequestFactory authRequestFactory,
        BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authServiceClient = authServiceClient;
        this.authRequestFactory = authRequestFactory;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(String username, String email, String name, String phoneNumber, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Un utilisateur avec cet email existe déjà: " + email);
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Un utilisateur avec ce nom d'utilisateur existe déjà: " + username);
        }

        // Créer un user temporaire avec mot de passe en clair pour l'API externe
        User tempUser = User.builder()
            .username(username)
            .email(email)
            .name(name)
            .phoneNumber(phoneNumber)
            .password(password)
            .build();

        ExternalAuthRegisterResponse externalResponse = authServiceClient
            .register(authRequestFactory.createRegisterRequest(tempUser))
            .block();

        if (externalResponse == null || !externalResponse.isSuccess()) {
            throw new AuthenticationException("Échec de l'enregistrement dans le service externe: " +
                (externalResponse != null ? externalResponse.getMessage() : "Réponse nulle"));
        }

        // Créer l'utilisateur réel avec mot de passe haché
        User newUser = User.builder()
            .id(UUID.randomUUID())
            .username(username)
            .email(email)
            .name(name)
            .phoneNumber(phoneNumber)
            .password(passwordEncoder.encode(password))
            .role(role)
            .externalId(externalResponse.getId())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .active(true)
            .build();

        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();

        // Vérifier si le mot de passe correspond
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Optional.empty();
        }

        // Appel à l'API externe avec mot de passe en clair
        ExternalAuthLoginResponse externalResponse = authServiceClient
            .login(authRequestFactory.createLoginRequest(username, password))
            .block();

        if (externalResponse == null || !externalResponse.isSuccess()) {
            throw new AuthenticationException("Échec de l'authentification dans le service externe: " +
                (externalResponse != null ? externalResponse.getMessage() : "Réponse nulle"));
        }

        return optionalUser;
    }

    @Override
    public void logout(String token) {
        authServiceClient.logout(token).block();
    }

    @Override
    public boolean validateToken(String token) {
        return authServiceClient.validateToken(token).block();
    }

    @Override
    public AuthenticationResult registerWithToken(String username, String email, String name, String phoneNumber, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Un utilisateur avec cet email existe déjà: " + email);
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Un utilisateur avec ce nom d'utilisateur existe déjà: " + username);
        }

        // Créer un user temporaire avec mot de passe en clair pour l'API externe
        User tempUser = User.builder()
            .username(username)
            .email(email)
            .name(name)
            .phoneNumber(phoneNumber)
            .password(password) // Mot de passe en clair
            .build();

        ExternalAuthRegisterResponse externalResponse = authServiceClient
            .register(authRequestFactory.createRegisterRequest(tempUser))
            .block();

        if (externalResponse == null || !externalResponse.isSuccess()) {
            throw new AuthenticationException("Échec de l'enregistrement dans le service externe: " +
                (externalResponse != null ? externalResponse.getMessage() : "Réponse nulle"));
        }

        // Créer l'utilisateur réel avec mot de passe haché
        User newUser = User.builder()
            .id(UUID.randomUUID())
            .username(username)
            .email(email)
            .name(name)
            .phoneNumber(phoneNumber)
            .password(passwordEncoder.encode(password)) // Mot de passe haché
            .role(role)
            .externalId(externalResponse.getId())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .active(true)
            .build();

        User savedUser = userRepository.save(newUser);

        // Obtenir un token si nécessaire
        String token = externalResponse.getToken();

        // Si aucun token n'est retourné, faire un appel de login séparé pour en obtenir un
        if (token == null || token.isEmpty()) {
            ExternalAuthLoginResponse loginResponse = authServiceClient
                .login(authRequestFactory.createLoginRequest(email, password))
                .block();

            if (loginResponse != null && loginResponse.isSuccess()) {
                token = loginResponse.getToken();
            }
        }

        return new AuthenticationResult(savedUser, token);
    }

    @Override
    public Optional<AuthenticationResult> loginWithToken(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();

        // Vérifier si le mot de passe correspond
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Optional.empty(); // Mot de passe incorrect
        }

        // Appel à l'API externe avec mot de passe en clair
        ExternalAuthLoginResponse externalResponse = authServiceClient
            .login(authRequestFactory.createLoginRequest(email, password))
            .block();

        if (externalResponse == null || !externalResponse.isSuccess()) {
            throw new AuthenticationException("Échec de l'authentification dans le service externe: " +
                (externalResponse != null ? externalResponse.getMessage() : "Réponse nulle"));
        }

        return Optional.of(new AuthenticationResult(optionalUser.get(), externalResponse.getToken()));
    }
}
