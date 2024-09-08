package folletto.toyproject.domain.post.respository;

import folletto.toyproject.domain.post.entity.PostEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Page<PostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);


    Page<PostEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);

    @Query("SELECT p FROM PostEntity p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% ORDER BY p.createdAt DESC")
    List<PostEntity> searchByTitleOrContent(@Param("keyword") String keyword);
}
