package folletto.toyproject.domain.post.api;

import folletto.toyproject.domain.post.dto.PostListResponse;
import folletto.toyproject.domain.post.dto.PostRequest;
import folletto.toyproject.domain.post.dto.PostResponse;
import folletto.toyproject.domain.post.service.PostService;
import folletto.toyproject.global.dto.ResponseDto;
import folletto.toyproject.global.dto.SearchRequest;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.naming.directory.SearchResult;

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

@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostApi {

    private final PostService postService;

    @PostMapping()
    @RolesAllowed({"USER"})
    public ResponseDto<Void> createPost(
            @RequestBody @Valid PostRequest postRequest) {
        postService.createPost(postRequest);
        return ResponseDto.created();
    }

    @PutMapping("/{postId}")
    @RolesAllowed({"USER"})
    ResponseDto<Void> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostRequest postRequest) {
        postService.updatePost(postId, postRequest);
        return ResponseDto.ok();
    }

    @DeleteMapping("/{postId}")
    @RolesAllowed({"USER"})
    ResponseDto<Void> deletePost(
            @PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseDto.ok();
    }

    @GetMapping("/{postId}")
    @RolesAllowed({"USER"})
    ResponseDto<PostResponse> findPost(
            @PathVariable Long postId) {
        PostResponse postResponse = postService.findPost(postId);
        return ResponseDto.okWithData(postResponse);
    }

    @GetMapping()
    ResponseDto<PostListResponse> findPosts(
            @RequestParam String sort,
            Pageable pageable
    ) {
        return ResponseDto.okWithData(postService.findPosts(sort, pageable));
    }

    @GetMapping("/search")
    ResponseDto<List<PostResponse>> searchPosts(
            @ModelAttribute("q") SearchRequest searchRequest
    ) {
        return ResponseDto.okWithData(postService.searchPosts(searchRequest.q()));
    }
}
