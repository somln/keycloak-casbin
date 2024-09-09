package folletto.toyproject.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import folletto.toyproject.domain.comment.entity.CommentEntity;
import folletto.toyproject.domain.user.entity.UserEntity;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        String content,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        boolean isUpdated,
        Long userId,
        String username
) {
    public static CommentResponse of(CommentEntity comment, UserEntity user) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.isUpdated(),
                user.getUserId(),
                user.getUsername()
        );
    }
}
