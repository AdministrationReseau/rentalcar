package inc.yowyob.rentalcar.infrastructure.external.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalAuthRegisterRequest {
    private String username;
    private String email;
    private String password;
    private String name;
    private String phone_number;
}
