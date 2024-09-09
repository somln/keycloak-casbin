package folletto.toyproject.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import folletto.toyproject.domain.post.entity.PostEntity;
import folletto.toyproject.domain.user.entity.UserEntity;

import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        String title,
        String content,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        boolean isUpdated,
        Long userId,
        String username
) {
    public static PostResponse from(PostEntity post, UserEntity user) {
        return new PostResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.isUpdated(),
                user.getUserId(),
                user.getUsername()
        );
    }
}
