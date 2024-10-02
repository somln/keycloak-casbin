package folletto.toyproject.domain.post.respository;

import folletto.toyproject.domain.post.entity.PostEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Page<PostEntity> findAllByGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);


    Page<PostEntity> findAllByGroupIdOrderByCreatedAtAsc(Long groupId, Pageable pageable);

    @Query("SELECT p FROM PostEntity p WHERE " +
            "p.groupId = :groupId AND " +
            "(p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
            "ORDER BY p.createdAt DESC")
    List<PostEntity> searchByTitleOrContent(@Param("groupId") Long groupId, @Param("keyword") String keyword);

    void deleteAllByGroupId(Long groupId);
}
