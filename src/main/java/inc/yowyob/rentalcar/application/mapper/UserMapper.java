package inc.yowyob.rentalcar.application.mapper;

import inc.yowyob.rentalcar.application.dto.UserDTO;
import inc.yowyob.rentalcar.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .name(user.getName())
            .phoneNumber(user.getPhoneNumber())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .active(user.isActive())
            .build();
    }
}
