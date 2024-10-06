package folletto.toyproject.domain.user.repository;

import folletto.toyproject.domain.user.entity.UserEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByUserUUID(String userUUID);

    List<UserEntity> findByGroupId(Long groupId);

    void deleteAllByGroupId(Long groupId);

   List<UserEntity> findByGroupIdAndIsMasterUser(Long groupId, boolean isMasterUse);
}
