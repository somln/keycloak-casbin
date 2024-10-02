package folletto.toyproject.global.auth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KeycloakProperties {

    @Value("${keycloak.auth-server-url}")
    private String hostUrl;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${custom-keycloak.admin_username}")
    private String username;

    @Value("${custom-keycloak.admin_password}")
    private String password;

    @Value("${custom-keycloak.grant_type}")
    private String grantType;

    @Value("${custom-keycloak.get_token_url}")
    private String getTokenUrl;

    @Value("${custom-keycloak.signup_url}")
    private String signupUrl;

    @Value("${custom-keycloak.user_role}")
    private String rolename;

    @Value("${custom-keycloak.uesr_role_id}")
    private String roleId;

    @Value("${custom-keycloak.client_uuid}")
    private String clientUUID;
}
