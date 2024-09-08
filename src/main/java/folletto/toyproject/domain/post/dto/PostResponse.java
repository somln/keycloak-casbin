package folletto.toyproject.domain.post.dto;

import folletto.toyproject.domain.post.entity.PostEntity;

public record PostResponse (
        Long postId,
        String title,
        String content,
        boolean isUpdated,
        Long userId
){
    public static PostResponse from(PostEntity postEntity){
        return new PostResponse(
                postEntity.getPostId(),
                postEntity.getTitle(),
                postEntity.getContent(),
                postEntity.isUpdated(),
                postEntity.getUserId()
        );
    }
}
