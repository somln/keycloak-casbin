package folletto.toyproject.domain.group.repository;

import folletto.toyproject.domain.group.entity.GroupEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    boolean existsByGroupName(String groupName);

    GroupEntity findByIsMasterGroup(boolean b);
}
