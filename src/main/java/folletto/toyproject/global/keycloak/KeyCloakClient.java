package folletto.toyproject.global.keycloak;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import folletto.toyproject.domain.user.dto.KeycloakSignupRequest;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;
import folletto.toyproject.global.http.HttpClient;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
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
            throw new RuntimeException("Failed to get token from KeyCloak", e);
        }
    }

    private Map<String, String> createTokenRequestParams() {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", keycloakProperties.getGrantType());
        formParams.put("client_id", keycloakProperties.getClientId());
        formParams.put("client_secret", keycloakProperties.getClientSecret());
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
            Response response = httpClient.post(buildUrl(keycloakProperties.getSignupUrl()), token,
                    signupDto);

            String location = response.header("Location");

            String userUUID = location.substring(location.lastIndexOf("/") + 1);
            return userUUID;
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.SIGNUP_FAILED);
        }
    }

    public String validateToken(String token) {
        try {
            String accessToken = token.replace("bearer ", "");
            Map<String, String> formParams = createTokenValidationParams(accessToken);
            String responseBody = httpClient.postForm(buildUrl(keycloakProperties.getValidateTokenUrl()), formParams).body().string();
            return parseTokenResponse(responseBody);
        } catch (IOException e) {
            throw new RuntimeException("Failed to validate token with Keycloak", e);
        }
    }

    private Map<String, String> createTokenValidationParams(String accessToken) {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", keycloakProperties.getClientId());
        formParams.put("client_secret", keycloakProperties.getClientSecret());
        formParams.put("token", accessToken);
        return formParams;
    }

    private String parseTokenResponse(String responseBody) {
        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

        if (jsonObject.get("active").getAsBoolean()) {
            if(jsonObject.get("azp").getAsString().equals(keycloakProperties.getClientId())) {
                return jsonObject.get("sub").getAsString();
            } else {
                throw new ApplicationException(ErrorCode.SESSION_INVALID);
            }
        } else {
            throw new ApplicationException(ErrorCode.SESSION_INVALID);
        }
    }

    private String buildUrl(String endpoint) {
        return keycloakProperties.getHostUrl() + endpoint;
    }
}
