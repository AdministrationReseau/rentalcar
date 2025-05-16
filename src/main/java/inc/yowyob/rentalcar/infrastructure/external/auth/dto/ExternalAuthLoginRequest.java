package inc.yowyob.rentalcar.infrastructure.external.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalAuthLoginRequest {
    private String username;
    private String password;
}
