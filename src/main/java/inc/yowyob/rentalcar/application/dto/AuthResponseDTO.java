package inc.yowyob.rentalcar.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private UserDTO user;
    private String token;
    private boolean success;
    private String message;
}
