package folletto.toyproject.domain.user.service;

import static folletto.toyproject.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static folletto.toyproject.global.exception.ErrorCode.USERNAME_ALREADY_EXISTS;

import folletto.toyproject.domain.user.dto.SignupRequest;
import folletto.toyproject.domain.user.dto.UserResponse;
import folletto.toyproject.domain.user.entity.UserEntity;
import folletto.toyproject.domain.user.repository.UserRepository;
import folletto.toyproject.global.casbin.AuthorizationManager;
import folletto.toyproject.global.casbin.ActionType;
import folletto.toyproject.global.casbin.ObjectType;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;
import folletto.toyproject.global.keycloak.KeyCloakClient;
import folletto.toyproject.global.keycloak.KeycloakSignupRequest;
import java.util.Objects;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    public UserService(UserRepository userRepository, KeyCloakClient keyCloakClient,
            AuthorizationManager authorizationManager) {
        this.userRepository = userRepository;
        this.keyCloakClient = keyCloakClient;
        this.authorizationManager = authorizationManager;
    }

    private final UserRepository userRepository;
    private final KeyCloakClient keyCloakClient;
    private final AuthorizationManager authorizationManager;

    @Transactional
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
        KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String userUUID = principal.getName();
        return findUserByUUID(userUUID);
    }

    private UserEntity findUserByUUID(String userUUID) {
        return userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    public List<UserResponse> findUserByGroup(Long groupId) {
        authorizationManager.verify(ObjectType.USER, ActionType.READ, groupId);
        return userRepository.findByGroupId(groupId).stream()
                .map(UserResponse::from).toList();
    }

    public UserResponse findUser(Long userId) {
        UserEntity user = findUserById(userId);
        Long groupId = user.getGroupId();
        authorizationManager.verify(ObjectType.USER, ActionType.READ, groupId);
        return UserResponse.from(user);
    }

    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void setMasterUser(Long userId, Long groupId) {
        UserEntity user = findUserById(userId);

        authorizationManager.verify(ObjectType.ROLE, ActionType.UPDATE, groupId);
        validateGroupUser(groupId, user);

        user.setMasterUser();
        authorizationManager.addRole(user.getUsername(), groupId);
    }

    private void validateGroupUser(Long groupId, UserEntity user) {
        if (!Objects.equals(user.getGroupId(), groupId)) {
            throw new ApplicationException(ErrorCode.USER_NOT_IN_GROUP);
        }
    }

    @Transactional
    public void unsetMasterUser(Long userId, Long groupId) {
        UserEntity user = findUserById(userId);

        authorizationManager.verify(ObjectType.ROLE, ActionType.UPDATE, groupId);
        validateGroupUser(groupId, user);

        user.unsetMasterUser();
        authorizationManager.deleteRole(user.getUsername(), groupId);
    }
}
