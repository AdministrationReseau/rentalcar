package inc.yowyob.rentalcar.infrastructure.external.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExternalAuthLoginResponse {
    private String id;
    private String username;
    private String email;
    private String token;
    private boolean success;
    private String message;
}
