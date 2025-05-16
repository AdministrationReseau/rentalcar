package inc.yowyob.rentalcar.presentation.mapper;

import inc.yowyob.rentalcar.application.dto.AuthResponseDTO;
import inc.yowyob.rentalcar.application.dto.LoginUserDTO;
import inc.yowyob.rentalcar.application.dto.RegisterUserDTO;
import inc.yowyob.rentalcar.application.dto.UserDTO;
import inc.yowyob.rentalcar.presentation.dto.AuthenticationResponse;
import inc.yowyob.rentalcar.presentation.dto.UserLoginRequest;
import inc.yowyob.rentalcar.presentation.dto.UserRegistrationRequest;
import inc.yowyob.rentalcar.presentation.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public RegisterUserDTO toRegisterUserDTO(UserRegistrationRequest request) {
        return RegisterUserDTO.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .name(request.getName())
            .phoneNumber(request.getPhoneNumber())
            .password(request.getPassword())
            .role(request.getRole())
            .build();
    }

    public LoginUserDTO toLoginUserDTO(UserLoginRequest request) {
        return LoginUserDTO.builder()
            .username(request.getUsername())
            .password(request.getPassword())
            .build();
    }

    public AuthenticationResponse toAuthenticationResponse(AuthResponseDTO dto) {
        return AuthenticationResponse.builder()
            .user(dto.getUser() != null ? toUserResponse(dto.getUser()) : null)
            .token(dto.getToken())
            .success(dto.isSuccess())
            .message(dto.getMessage())
            .build();
    }

    public UserResponse toUserResponse(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        return UserResponse.builder()
            .id(dto.getId())
            .username(dto.getUsername())
            .email(dto.getEmail())
            .name(dto.getName())
            .phoneNumber(dto.getPhoneNumber())
            .role(dto.getRole())
            .createdAt(dto.getCreatedAt())
            .active(dto.isActive())
            .build();
    }
}
