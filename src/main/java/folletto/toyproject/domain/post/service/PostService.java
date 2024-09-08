package folletto.toyproject.domain.post.service;

import folletto.toyproject.domain.post.dto.PostListResponse;
import folletto.toyproject.domain.post.dto.PostRequest;
import folletto.toyproject.domain.post.dto.PostResponse;
import folletto.toyproject.domain.post.entity.PostEntity;
import folletto.toyproject.domain.post.respository.PostRepository;
import folletto.toyproject.domain.user.entity.UserEntity;
import folletto.toyproject.domain.user.repository.UserRepository;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;
import folletto.toyproject.global.keycloak.KeyCloakClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final KeyCloakClient keyCloakClient;

    @Transactional
    public void createPost(PostRequest createPostRequest, String token) {
        String userUUID = keyCloakClient.validateToken(token);
        UserEntity user = findUserByUUID(userUUID);
        postRepository.save(PostEntity.of(createPostRequest, user.getUserId()));
    }

    private UserEntity findUserByUUID(String userUUID) {
        return userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void updatePost(Long postId, PostRequest postRequest, String token) {
        String userUUID = keyCloakClient.validateToken(token);
        UserEntity user = findUserByUUID(userUUID);
        PostEntity post = findPostById(postId);
        validateAuthor(user, post);
        post.update(postRequest);
    }

    private PostEntity findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND));
    }

    public void validateAuthor(UserEntity user, PostEntity post) {
        if (!post.getUserId().equals(user.getUserId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    @Transactional
    public void deletePost(Long postId, String token) {
        String userUUID = keyCloakClient.validateToken(token);
        UserEntity user = findUserByUUID(userUUID);
        PostEntity post = findPostById(postId);
        validateAuthor(user, post);
        postRepository.delete(post);
    }

    public PostResponse findPost(Long postId, String token) {
        String userUUID = keyCloakClient.validateToken(token);
        UserEntity user = findUserByUUID(userUUID);
        return PostResponse.from(findPostById(postId), user);
    }

    public PostListResponse findPosts(String sort, Pageable pageable, String token) {
        String userUUID = keyCloakClient.validateToken(token);
        Page<PostEntity> posts;
        if (SortType.fromDescription(sort).equals(SortType.DESC)) {
            posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else {
            posts = postRepository.findAllByOrderByCreatedAtAsc(pageable);
        }
        UserEntity user = findUserByUUID(userUUID);
        List<PostResponse> postResponses = posts.getContent().stream()
                .map(post -> PostResponse.from(post, user)).
                toList();
        return PostListResponse.from(postResponses, posts);
    }

    public List<PostResponse> searchPosts(String keyword, String token) {
        String userUUID = keyCloakClient.validateToken(token);
        UserEntity user = findUserByUUID(userUUID);
        List<PostEntity> posts = postRepository.searchByTitleOrContent(keyword);
        return posts.stream().map(post -> PostResponse.from(post, user)).toList();
    }
}
