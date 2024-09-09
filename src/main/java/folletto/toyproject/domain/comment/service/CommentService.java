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
        UserEntity user = authenticateUser(token);
        findPostById(postId);
        commentRepository.save(CommentEntity.of(commentRequest.content(), user.getUserId(), postId));
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequest commentRequest, String token) {
        UserEntity user = authenticateUser(token);
        CommentEntity comment = findCommentById(commentId);
        validateAuthor(user, comment);
        comment.update(commentRequest.content());
    }

    @Transactional
    public void deleteComment(Long commentId, String token) {
        UserEntity user = authenticateUser(token);
        CommentEntity comment = findCommentById(commentId);
        validateAuthor(user, comment);
        commentRepository.delete(comment);
    }

    public List<CommentResponse> findComments(Long postId, String token) {
        authenticateUser(token);
        List<CommentEntity> comments = commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId);
        return comments.stream()
                .map(comment -> {
                    UserEntity user = findUserById(comment.getUserId());
                    return CommentResponse.of(comment, user);
                })
                .toList();
    }

    private UserEntity authenticateUser(String token) {
        String userUUID = keyCloakClient.validateToken(token);
        return findUserByUUID(userUUID);
    }

    private PostEntity findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
    }

    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    private UserEntity findUserByUUID(String userUUID) {
        return userRepository.findByUserUUID(userUUID)
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
