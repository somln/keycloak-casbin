package folletto.toyproject.global.keycloak;

import com.google.gson.Gson;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;
import folletto.toyproject.global.http.HttpClient;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeyCloakClient {

    private final KeycloakProperties keycloakProperties;
    private final HttpClient httpClient;
    private final Gson gson;
    private String token;

    public KeyCloakClient(KeycloakProperties keycloakProperties, HttpClient httpClient) {
        this.keycloakProperties = keycloakProperties;
        this.httpClient = httpClient;
        this.gson = new Gson();
        init();
    }

    private void init() {
        this.token = getNewAdminToken();
    }

    public String getNewAdminToken() {
        try {
            Map<String, String> formParams = createTokenRequestParams();
            String responseBody = httpClient.postForm(buildUrl(keycloakProperties.getGetTokenUrl()), formParams).body().string();
            return extractTokenFromResponse(responseBody);
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.TOKEN_REQUEST_FAILED);
        }
    }

    private Map<String, String> createTokenRequestParams() {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", keycloakProperties.getGrantType());
        formParams.put("client_id", keycloakProperties.getClientId());
        formParams.put("username", keycloakProperties.getUsername());
        formParams.put("password", keycloakProperties.getPassword());
        return formParams;
    }

    private String extractTokenFromResponse(String responseBody) {
        Map<String, String> map = gson.fromJson(responseBody, Map.class);
        return map.get("access_token");
    }

    public String signup(KeycloakSignupRequest signupDto) {
        try {
            String token = getNewAdminToken();
            Response response = httpClient.post(buildUrl(keycloakProperties.getSignupUrl()), token, signupDto);
            return extractUserUUIDFromResponse(response);
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.SIGNUP_FAILED);
        }
    }

    private String extractUserUUIDFromResponse(Response response) {
        String location = response.header("Location");
        return location != null ? location.substring(location.lastIndexOf("/") + 1) : null;
    }

    private String buildUrl(String endpoint) {
        return keycloakProperties.getHostUrl() + endpoint;
    }

    public void mappingRole(String userUUID) {
        try {
            List<KeycloakRole> roles = List.of(
                    KeycloakRole.of(keycloakProperties.getRoleId(), keycloakProperties.getRolename())
            );
            httpClient.post(keycloakProperties.getHostUrl() + "/admin/realms/board/users/" + userUUID +
                    "/role-mappings/clients/" + keycloakProperties.getClientUUID(), token, roles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map roles for user: " + userUUID, e);
        }
    }
}
