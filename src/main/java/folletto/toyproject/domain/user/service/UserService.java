package folletto.toyproject.domain.user.service;

import static folletto.toyproject.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static folletto.toyproject.global.exception.ErrorCode.USERNAME_ALREADY_EXISTS;

import folletto.toyproject.domain.user.dto.KeycloakSignupRequest;
import folletto.toyproject.domain.user.dto.SignupRequest;
import folletto.toyproject.domain.user.repository.UserRepository;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.keycloak.KeyCloakClient;
import org.springframework.stereotype.Service;

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
        userRepository.save(signupRequest.toEntity(userUUID));
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
}
