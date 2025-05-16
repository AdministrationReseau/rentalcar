package inc.yowyob.rentalcar.infrastructure.persistence.repository;

import inc.yowyob.rentalcar.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCassandraRepository extends CassandraRepository<UserEntity, UUID> {

    @AllowFiltering
    Optional<UserEntity> findByEmail(String email);

    @AllowFiltering
    Optional<UserEntity> findByUsername(String username);

    @AllowFiltering
    List<UserEntity> findByRole(String role);
}
