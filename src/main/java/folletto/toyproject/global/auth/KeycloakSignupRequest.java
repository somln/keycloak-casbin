package folletto.toyproject.global.auth;

import folletto.toyproject.domain.user.dto.CredentialRepresentation;
import folletto.toyproject.domain.user.dto.SignupRequest;

import java.util.List;

public record KeycloakSignupRequest(
        String username,
        String email,
        List<CredentialRepresentation> credentials,
        boolean enabled
) {

    public static KeycloakSignupRequest from(SignupRequest signupRequest) {
        return new KeycloakSignupRequest(
                signupRequest.username(),
                signupRequest.email(),
                List.of(new CredentialRepresentation("password", signupRequest.password(), false)),
                true
        );
    }
}