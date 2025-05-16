package inc.yowyob.rentalcar.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String username;
    private String email;
    private String name;
    private String phoneNumber;
    private String password;
    private String role;
    private String externalId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
