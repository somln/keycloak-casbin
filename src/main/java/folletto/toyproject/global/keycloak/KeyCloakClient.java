package folletto.toyproject.global.keycloak;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import folletto.toyproject.domain.user.dto.SignupRequestDto;
import folletto.toyproject.global.http.HttpClient;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class KeyCloakClient {

    private final KeycloakProperties keycloakProperties;
    private final HttpClient httpClient;

    public KeyCloakClient(KeycloakProperties keycloakProperties, HttpClient httpClient) {
        this.keycloakProperties = keycloakProperties;
        this.httpClient = httpClient;
    }

    private String token;

    public void init() {
        this.token = getNewToken();
    }

    public String getNewToken() {
        try {
            Map<String, String> formParams = new HashMap<>();
            formParams.put("grant_type", keycloakProperties.getGrantType());
            formParams.put("client_id", keycloakProperties.getClientId());
            formParams.put("client_secret", keycloakProperties.getClientSecret());
            formParams.put("username", keycloakProperties.getUsername());
            formParams.put("password", keycloakProperties.getPassword());

            String responseBody = httpClient.postForm(keycloakProperties.getHostUrl() + keycloakProperties.getGetTokenUrl(), formParams).body().string();
            return extractTokenFromResponse(responseBody);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get token from KeyCloak", e);
        }
    }

    private String extractTokenFromResponse(String responseBody) {
        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(responseBody, Map.class);
        return map.get("access_token");
    }

    public Response signup(SignupRequestDto signupDto) throws IOException {
        return httpClient.post(keycloakProperties.getHostUrl() + keycloakProperties.getSignupUrl(), token, signupDto);
    }

    public String validateToken(String accessToken) {
        try {
            String url = keycloakProperties.getHostUrl() + keycloakProperties.getValidateTokenUrl();
            Map<String, String> formParams = new HashMap<>();
            formParams.put("client_id", keycloakProperties.getClientId());
            formParams.put("client_secret", keycloakProperties.getClientSecret());
            formParams.put("token", accessToken);
            String responseBody = httpClient.postForm(url, formParams).body().string();

            return parseTokenResponse(responseBody);
        } catch (IOException e) {
            throw new RuntimeException("Failed to validate token with Keycloak", e);
        }
    }

    public String parseTokenResponse(String responseBody) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

        // `active` 필드가 불리언 타입인 경우 직접 확인
        boolean isActive = jsonObject.get("active").getAsBoolean();

        if (isActive) {
            return jsonObject.get("sub").getAsString();
        }
        return null;
    }
}
