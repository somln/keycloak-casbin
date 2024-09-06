package folletto.toyproject.global.keycloak;

import com.google.gson.Gson;
import folletto.toyproject.domain.user.dto.SignupRequestDto;
import folletto.toyproject.global.http.HttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class KeyCloakClient {

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


    private String token;

    private final HttpClient httpClient = new HttpClient();


    public void init() {
        this.token = getNewToken();
    }

    public String getNewToken() {
        try {
            Map<String, String> formParams = new HashMap<>();
            formParams.put("grant_type", grantType);
            formParams.put("client_id", clientId);
            formParams.put("username", username);
            formParams.put("password", password);

            String responseBody = httpClient.postForm(hostUrl + getTokenUrl, formParams).body().string();
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
        return httpClient.post(hostUrl + signupUrl, token, signupDto);
    }

}
