package folletto.toyproject.domain.comment.api;

import folletto.toyproject.domain.comment.dto.CommentRequest;
import folletto.toyproject.domain.comment.dto.CommentResponse;
import folletto.toyproject.domain.comment.service.CommentService;
import folletto.toyproject.global.dto.ResponseDto;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@RolesAllowed({"USER"})
public class CommentApi {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseDto<Void> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest commentRequest
    ) {
        commentService.createComment(postId, commentRequest);
        return ResponseDto.created();
    }

    @PutMapping("/comments/{commentId}")
    public ResponseDto<Void> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest commentRequest) {
        commentService.updateComment(commentId, commentRequest);
        return ResponseDto.ok();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseDto<Void> deleteComment(
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseDto.ok();
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseDto<List<CommentResponse>> findComments(
            @PathVariable Long postId) {
        List<CommentResponse> comments = commentService.findComments(postId);
        return ResponseDto.okWithData(comments);
    }
}
