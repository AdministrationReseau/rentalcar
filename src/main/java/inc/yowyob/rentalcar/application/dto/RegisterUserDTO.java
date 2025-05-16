package inc.yowyob.rentalcar.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    private String username;
    private String email;
    private String name;
    private String phoneNumber;
    private String password;
    private String role;
}
