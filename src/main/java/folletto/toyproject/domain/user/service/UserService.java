package folletto.toyproject.domain.user.service;

import static folletto.toyproject.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static folletto.toyproject.global.exception.ErrorCode.USERNAME_ALREADY_EXISTS;

import folletto.toyproject.domain.user.dto.SignupRequest;
import folletto.toyproject.domain.user.dto.UserResponse;
import folletto.toyproject.domain.user.entity.UserEntity;
import folletto.toyproject.domain.user.repository.UserRepository;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;
import folletto.toyproject.global.keycloak.KeyCloakClient;
import folletto.toyproject.global.keycloak.KeycloakSignupRequest;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KeyCloakClient keyCloakClient;

    public UserService(UserRepository userRepository, KeyCloakClient keyCloakClient) {
        this.userRepository = userRepository;
        this.keyCloakClient = keyCloakClient;
    }

    public void signUp(SignupRequest signupRequest) {
        validateDuplicateUser(signupRequest);
        String userUUID = keyCloakClient.signup(KeycloakSignupRequest.from(signupRequest));
        keyCloakClient.mappingRole(userUUID);
        userRepository.save(UserEntity.from(signupRequest, userUUID));
    }

    private void validateDuplicateUser(SignupRequest signupRequest) {
        validateUsername(signupRequest.username());
        validateEmail(signupRequest.email());
    }

    private void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new ApplicationException(USERNAME_ALREADY_EXISTS);
        }
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ApplicationException(EMAIL_ALREADY_EXISTS);
        }
    }

    public UserResponse findMyInfo() {
        UserEntity user = findCurrentUser();
        return UserResponse.from(user);
    }

    private UserEntity findCurrentUser() {
        KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userUUID = principal.getName();
        return findUserByUUID(userUUID);
    }

    private UserEntity findUserByUUID(String userUUID) {
        return userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    public List<UserResponse> findUserByGroup(Long groupId) {
        return userRepository.findByGroupId(groupId).stream()
                .map(UserResponse::from).toList();
    }

    public UserResponse findUser(Long userId) {
        UserEntity user = findUserById(userId);
        return UserResponse.from(user);
    }

    private UserEntity findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

}
