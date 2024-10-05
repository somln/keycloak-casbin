package folletto.toyproject.domain.comment.service;

import static folletto.toyproject.global.casbin.ActionType.CREATE;
import static folletto.toyproject.global.casbin.ActionType.DELETE;
import static folletto.toyproject.global.casbin.ActionType.READ;
import static folletto.toyproject.global.casbin.ActionType.UPDATE;
import static folletto.toyproject.global.casbin.ObjectType.BOARD;

import folletto.toyproject.domain.comment.dto.CommentRequest;
import folletto.toyproject.domain.comment.dto.CommentResponse;
import folletto.toyproject.domain.comment.entity.CommentEntity;
import folletto.toyproject.domain.comment.repository.CommentRepository;
import folletto.toyproject.domain.post.entity.PostEntity;
import folletto.toyproject.domain.post.respository.PostRepository;
import folletto.toyproject.domain.user.entity.UserEntity;
import folletto.toyproject.domain.user.repository.UserRepository;
import folletto.toyproject.global.casbin.AuthorizationManager;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthorizationManager authorizationManager;

    @Transactional
    public void createComment(Long postId, CommentRequest commentRequest) {
        PostEntity post = findPostById(postId);
        UserEntity currentUser = authorizationManager.verify(BOARD, CREATE, post.getGroupId());
        commentRepository.save(CommentEntity.of(commentRequest.content(), currentUser.getUserId(), postId));
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequest commentRequest) {
        CommentEntity comment = findCommentById(commentId);
        PostEntity post = findPostById(comment.getPostId());

        UserEntity currentUser = authorizationManager.verify(BOARD, UPDATE, post.getGroupId());
        validateAuthor(currentUser, comment);

        comment.update(commentRequest.content());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        CommentEntity comment = findCommentById(commentId);
        PostEntity post = findPostById(comment.getPostId());

        UserEntity currentUser = authorizationManager.verify(BOARD, DELETE, post.getGroupId());
        validateAuthor(currentUser, comment);

        commentRepository.delete(comment);
    }

    public List<CommentResponse> findComments(Long postId) {
        PostEntity post = findPostById(postId);
        authorizationManager.verify(BOARD, READ, post.getGroupId());

        List<CommentEntity> comments = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
        return comments.stream()
                .map(comment -> {
                    UserEntity user = findUserById(comment.getUserId());
                    return CommentResponse.of(comment, user);
                })
                .toList();
    }

    private PostEntity findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
    }

    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    private CommentEntity findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateAuthor(UserEntity user, CommentEntity comment) {
        if (!comment.getUserId().equals(user.getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

}
