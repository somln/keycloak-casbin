package folletto.toyproject.domain.post.entity;

import folletto.toyproject.domain.post.dto.PostRequest;
import folletto.toyproject.global.entity.BaseTimeEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "posts")
public class PostEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;
    private String content;
    private boolean isUpdated;

    private Long userId;
    private Long groupId;

    @Builder
    public PostEntity(Long groupId, Long userId, boolean isUpdated, String content, String title) {
        this.groupId = groupId;
        this.userId = userId;
        this.isUpdated = isUpdated;
        this.content = content;
        this.title = title;
    }

    public static PostEntity of(PostRequest postRequest, Long userId, Long groupId) {
        return PostEntity.builder()
                .title(postRequest.title())
                .content(postRequest.content())
                .isUpdated(false)
                .userId(userId)
                .groupId(groupId)
                .build();
    }

    public void update(PostRequest postRequest) {
        this.title = postRequest.title();
        this.content = postRequest.content();
        this.isUpdated = true;
    }
}
