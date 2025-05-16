package inc.yowyob.rentalcar.infrastructure.persistence.adapter;

import inc.yowyob.rentalcar.domain.model.User;
import inc.yowyob.rentalcar.domain.repository.UserRepository;
import inc.yowyob.rentalcar.infrastructure.persistence.entity.UserEntity;
import inc.yowyob.rentalcar.infrastructure.persistence.repository.UserCassandraRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserCassandraRepository userRepository;

    public UserRepositoryAdapter(UserCassandraRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = toEntity(user);
        if (userEntity.getId() == null) {
            userEntity.setId(UUID.randomUUID());
        }

        if (userEntity.getCreatedAt() == null) {
            userEntity.setCreatedAt(LocalDateTime.now());
        }

        userEntity.setUpdatedAt(LocalDateTime.now());

        UserEntity savedEntity = userRepository.save(userEntity);
        return toModel(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toModel);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toModel);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream()
            .map(this::toModel)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findByRole(String role) {
        return userRepository.findByRole(role).stream()
            .map(this::toModel)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .name(user.getName())
            .phoneNumber(user.getPhoneNumber())
            .password(user.getPassword())
            .role(user.getRole())
            .externalId(user.getExternalId())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .active(user.isActive())
            .build();
    }

    private User toModel(UserEntity entity) {
        return User.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .email(entity.getEmail())
            .name(entity.getName())
            .phoneNumber(entity.getPhoneNumber())
            .password(entity.getPassword())
            .role(entity.getRole())
            .externalId(entity.getExternalId())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .active(entity.isActive())
            .build();
    }
}
