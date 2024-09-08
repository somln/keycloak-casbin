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
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "post")
public class PostEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;
    private String content;
    private Long userId;
    private boolean isUpdated;

    @Builder
    public PostEntity(String title, String content, Long userId, boolean isUpdated) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.isUpdated =  isUpdated;
    }

    public static PostEntity from(PostRequest postRequest, Long userId) {
        return PostEntity.builder()
                .title(postRequest.title())
                .content(postRequest.content())
                .userId(userId)
                .isUpdated(false)
                .build();
    }

    public void update(PostRequest postRequest) {
        this.title = postRequest.title();
        this.content = postRequest.content();
        this.isUpdated = true;
    }
}
