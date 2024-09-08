package folletto.toyproject.domain.post.api;

import folletto.toyproject.domain.post.dto.PostListResponse;
import folletto.toyproject.domain.post.dto.PostRequest;
import folletto.toyproject.domain.post.dto.PostResponse;
import folletto.toyproject.domain.post.service.PostService;
import folletto.toyproject.global.dto.ResponseDto;
import folletto.toyproject.global.dto.SearchRequest;
import java.util.List;
import javax.naming.directory.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public ResponseDto<Void> createPost(
            @RequestBody PostRequest postRequest,
            @RequestHeader("Authorization") String token) {
        postService.createPost(postRequest, token);
        return ResponseDto.created();
    }

    @PutMapping("/{postId}")
    ResponseDto<Void> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequest postRequest,
            @RequestHeader("Authorization") String token) {
        postService.updatePost(postId, postRequest, token);
        return ResponseDto.ok();
    }

    @DeleteMapping("/{postId}")
    ResponseDto<Void> deletePost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {
        postService.deletePost(postId, token);
        return ResponseDto.ok();
    }

    @GetMapping("/{postId}")
    ResponseDto<PostResponse> findPost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {
        PostResponse postResponse = postService.findPost(postId, token);
        return ResponseDto.okWithData(postResponse);
    }

    @GetMapping()
    ResponseDto<PostListResponse> findPosts(
            @RequestParam String sort,
            Pageable pageable,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseDto.okWithData(postService.findPosts(sort, pageable, token));
    }

    @GetMapping("/search")
    ResponseDto<List<PostResponse>> searchPosts(
            @ModelAttribute("q") SearchRequest searchRequest,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseDto.okWithData(postService.searchPosts(searchRequest.q(), token));
    }
}
