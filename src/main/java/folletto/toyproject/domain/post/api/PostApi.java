package folletto.toyproject.domain.post.api;

import folletto.toyproject.domain.post.dto.PostListResponse;
import folletto.toyproject.domain.post.dto.PostRequest;
import folletto.toyproject.domain.post.dto.PostResponse;
import folletto.toyproject.domain.post.service.PostService;
import folletto.toyproject.domain.user.entity.UserEntity;
import folletto.toyproject.global.auth.AuthorizationVerifier;
import folletto.toyproject.global.dto.ResponseDto;
import folletto.toyproject.global.dto.SearchRequest;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.naming.directory.SearchResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.KeycloakPrincipal;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class PostApi {

    private final PostService postService;
    private final AuthorizationVerifier authorizationVerifier;

    @PostMapping("groups/{groupId}/posts")
    @RolesAllowed({"USER"})
    public ResponseDto<Void> createPost(
            @PathVariable Long groupId,
            @RequestBody @Valid PostRequest postRequest,
            HttpServletRequest servletRequest

    ) {
        UserEntity currentUser = authorizationVerifier.verify(servletRequest, groupId);
        postService.createPost(groupId, postRequest, currentUser);
        return ResponseDto.created();
    }

    @PutMapping("posts/{postId}")
    @RolesAllowed({"USER"})
    ResponseDto<Void> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostRequest postRequest) {
        postService.updatePost(postId, postRequest);
        return ResponseDto.ok();
    }

    @DeleteMapping("posts/{postId}")
    @RolesAllowed({"USER"})
    ResponseDto<Void> deletePost(
            @PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseDto.ok();
    }

    @GetMapping("posts/{postId}")
    @RolesAllowed({"USER"})
    ResponseDto<PostResponse> findPost(
            @PathVariable Long postId) {
        PostResponse postResponse = postService.findPost(postId);
        return ResponseDto.okWithData(postResponse);
    }

    @GetMapping("groups/{groupId}/posts")
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
