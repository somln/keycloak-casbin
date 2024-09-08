package folletto.toyproject.domain.user.dto;

import java.util.Collections;
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
                Collections.singletonList(CredentialRepresentation.from(signupRequest.password())),
                true);
    }

}