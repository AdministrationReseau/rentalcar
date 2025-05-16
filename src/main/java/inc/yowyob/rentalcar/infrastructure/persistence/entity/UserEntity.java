package inc.yowyob.rentalcar.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @PrimaryKey
    private UUID id;

    @Column("username")
    private String username;

    @Column("email")
    private String email;

    @Column("name")
    private String name;

    @Column("phone_number")
    private String phoneNumber;

    @Column("password")
    private String password;

    @Column("role")
    private String role;

    @Column("external_id")
    private String externalId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("active")
    private boolean active;
}
