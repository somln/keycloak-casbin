package folletto.toyproject.domain.comment.service;

import folletto.toyproject.domain.comment.dto.CommentRequest;
import folletto.toyproject.domain.comment.dto.CommentResponse;
import folletto.toyproject.domain.comment.entity.CommentEntity;
import folletto.toyproject.domain.comment.repository.CommentRepository;
import folletto.toyproject.domain.post.entity.PostEntity;
import folletto.toyproject.domain.post.respository.PostRepository;
import folletto.toyproject.domain.user.entity.UserEntity;
import folletto.toyproject.domain.user.repository.UserRepository;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;
import folletto.toyproject.global.keycloak.KeyCloakClient;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final KeyCloakClient keyCloakClient;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository,
            UserRepository userRepository, KeyCloakClient keyCloakClient) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.keyCloakClient = keyCloakClient;
    }

    @Transactional
    public void createComment(Long postId, CommentRequest commentRequest, String token) {
        String userUUID = keyCloakClient.validateToken(token);
        findPostById(postId);
        UserEntity user = findUserByUUID(userUUID);
        commentRepository.save(CommentEntity.of(commentRequest.content(), user.getUserId(), postId));
    }

    private PostEntity findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
    }

    private UserEntity findUserByUUID(String userUUID) {
        return userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequest commentRequest, String token) {
        String userUUID = keyCloakClient.validateToken(token);
        UserEntity user = findUserByUUID(userUUID);
        CommentEntity comment = findCommentById(commentId);
        validateAuthor(user, comment);
        comment.update(commentRequest.content());
    }

    private CommentEntity findCommentById(Long postId) {
        return commentRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public void validateAuthor(UserEntity user, CommentEntity comment) {
        if (!comment.getUserId().equals(user.getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    @Transactional
    public void deleteComment(Long commentId, String token) {
        String userUUID = keyCloakClient.validateToken(token);
        UserEntity user = findUserByUUID(userUUID);
        CommentEntity comment = findCommentById(commentId);
        validateAuthor(user, comment);
        commentRepository.delete(comment);
    }

    public List<CommentResponse> findComments(Long postId, String token) {
        String userUUID = keyCloakClient.validateToken(token);
        List<CommentEntity> comments = commentRepository.findAllByPostIdOrderByCreatedAtAsc(
                postId);
        UserEntity userByUUID = findUserByUUID(userUUID);
        return comments.stream().map(comment -> CommentResponse.of(comment, userByUUID))
                .toList();
    }
}
