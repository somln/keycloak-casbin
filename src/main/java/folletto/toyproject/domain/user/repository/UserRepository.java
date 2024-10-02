package folletto.toyproject.domain.user.repository;

import folletto.toyproject.domain.user.entity.UserEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByUserUUID(String userUUID);

    Optional<UserEntity> findByGroupId(Long groupId);

    void deleteAllByGroupId(Long groupId);
}
