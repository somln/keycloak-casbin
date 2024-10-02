package folletto.toyproject.domain.comment.entity;

import folletto.toyproject.global.entity.BaseTimeEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "comments")
public class CommentEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String content;
    private boolean isUpdated;

    private Long userId;
    private Long postId;


    @Builder
    public CommentEntity(Long commentId, String content, boolean isUpdated, Long userId, Long postId) {
        this.commentId = commentId;
        this.content = content;
        this.isUpdated = isUpdated;
        this.userId = userId;
        this.postId = postId;
    }

    public static CommentEntity of(String content, Long userId, Long postId) {
        return CommentEntity.builder()
                .content(content)
                .isUpdated(false)
                .userId(userId)
                .postId(postId)
                .build();
    }

    public void update(String content) {
        this.content = content;
        this.isUpdated = true;
    }
}
