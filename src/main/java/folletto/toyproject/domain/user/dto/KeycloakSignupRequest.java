package folletto.toyproject.domain.user.dto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record KeycloakSignupRequest(
        String username,
        String email,
        List<CredentialRepresentation> credentials,
        Map<String, List<String>> clientRoles,
        boolean enabled
) {

    public static KeycloakSignupRequest from(SignupRequest signupRequest) {
        return new KeycloakSignupRequest(
                signupRequest.username(),
                signupRequest.email(),
                List.of(new CredentialRepresentation("password", signupRequest.password(), false)),
                Map.of("main_client", List.of("USER")),
                true
        );
    }
}