package inc.yowyob.rentalcar.infrastructure.external.auth.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalAuthRegisterResponse {
    private String id;
    private String username;
    private String email;
    private String token;
    private boolean success;
    private String message;
}
