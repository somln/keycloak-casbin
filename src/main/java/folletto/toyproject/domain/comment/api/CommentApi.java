package folletto.toyproject.domain.comment.api;

import folletto.toyproject.domain.comment.dto.CommentRequest;
import folletto.toyproject.domain.comment.dto.CommentResponse;
import folletto.toyproject.domain.comment.service.CommentService;
import folletto.toyproject.global.dto.ResponseDto;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CommentApi {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseDto<Void> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest commentRequest,
            @RequestHeader("Authorization") String token) {
        commentService.createComment(postId, commentRequest, token);
        return ResponseDto.created();
    }

    @PutMapping("/comments/{commentId}")
    public ResponseDto<Void>  updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest commentRequest,
            @RequestHeader("Authorization") String token) {
        commentService.updateComment(commentId, commentRequest, token);
        return ResponseDto.ok();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseDto<Void>  updateComment(
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String token) {
        commentService.deleteComment(commentId, token);
        return ResponseDto.ok();
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseDto<List<CommentResponse>> findComments(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {
        List<CommentResponse> comments = commentService.findComments(postId, token);
        return ResponseDto.okWithData(comments);
    }

}
