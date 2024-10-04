package folletto.toyproject.domain.post.api;

import static folletto.toyproject.global.dto.ActionType.*;
import static folletto.toyproject.global.dto.ObjectType.*;

import folletto.toyproject.domain.post.dto.PostListResponse;
import folletto.toyproject.domain.post.dto.PostRequest;
import folletto.toyproject.domain.post.dto.PostResponse;
import folletto.toyproject.domain.post.service.PostService;
import folletto.toyproject.domain.user.entity.UserEntity;
import folletto.toyproject.global.casbin.AuthorizationManager;
import folletto.toyproject.global.dto.ActionType;
import folletto.toyproject.global.dto.ObjectType;
import folletto.toyproject.global.dto.ResponseDto;
import folletto.toyproject.global.dto.SearchRequest;

import java.util.List;
import javax.annotation.security.RolesAllowed;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;



@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class PostApi {

    private final PostService postService;

    @PostMapping("/groups/{groupId}/posts")
    @RolesAllowed({"USER"})
    public ResponseDto<Void> createPost(
            @PathVariable Long groupId,
            @RequestBody @Valid PostRequest postRequest
    ) {
        postService.createPost(groupId, postRequest);
        return ResponseDto.created();
    }

    @PutMapping("/posts/{postId}")
    @RolesAllowed({"USER"})
    ResponseDto<Void> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostRequest postRequest) {
        postService.updatePost(postId, postRequest);
        return ResponseDto.ok();
    }

    @DeleteMapping("/posts/{postId}")
    @RolesAllowed({"USER"})
    ResponseDto<Void> deletePost(
            @PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseDto.ok();
    }

    @GetMapping("/posts/{postId}")
    @RolesAllowed({"USER"})
    ResponseDto<PostResponse> findPost(
            @PathVariable Long postId) {
        PostResponse postResponse = postService.findPost(postId);
        return ResponseDto.okWithData(postResponse);
    }

    @GetMapping("/posts/groups/{groupId}")
    ResponseDto<PostListResponse> findPosts(
            @PathVariable Long groupId,
            @RequestParam String sort,
            Pageable pageable
    ) {
        return ResponseDto.okWithData(postService.findPosts(groupId, sort, pageable));
    }

    @GetMapping("groups/{groupId}/search")
    ResponseDto<List<PostResponse>> searchPosts(
            @PathVariable Long groupId,
            @ModelAttribute("q") SearchRequest searchRequest
    ) {
        return ResponseDto.okWithData(postService.searchPosts(groupId, searchRequest.q()));
    }
}
