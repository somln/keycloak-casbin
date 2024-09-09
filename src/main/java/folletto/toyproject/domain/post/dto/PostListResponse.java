package folletto.toyproject.domain.post.dto;

import folletto.toyproject.domain.post.entity.PostEntity;

import java.util.List;

import org.springframework.data.domain.Page;

public record PostListResponse(
        Integer totalPageNumber,
        int nowPageNumber,
        boolean isLast,
        List<PostResponse> posts
) {
    public static PostListResponse from(List<PostResponse> postResponses, Page<PostEntity> postEntities) {
        return new PostListResponse(
                postEntities.getTotalPages(),
                postEntities.getNumber(),
                postEntities.isLast(),
                postResponses
        );
    }
}
