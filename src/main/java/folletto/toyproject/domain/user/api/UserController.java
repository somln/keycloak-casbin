package folletto.toyproject.domain.user.api;

import com.google.gson.Gson;
import folletto.toyproject.domain.user.dto.SignupRequestDto;
import folletto.toyproject.global.http.HttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/users")
@RestController
public class UserController {

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

    @Value("${custom-keycloak.signup_url}")
    private String signupUrl;

    @Value("${custom-keycloak.get_token_url}")
    private String getTokenUrl;

    private final HttpClient httpClient = new HttpClient();

    @PostMapping("/signup")
    public Response signup(@RequestBody SignupRequestDto signupDto) throws IOException {
        return httpClient.post(hostUrl + signupUrl, getToken(), signupDto
        );
    }

    public String getToken() throws IOException {

        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", grantType);
        formParams.put("client_id", clientId);
        formParams.put("username", username);
        formParams.put("password", password);

        String responseBody = httpClient.postForm(hostUrl + getTokenUrl, formParams).body().string();
        return extractTokenFromResponse(responseBody);
    }

    private String extractTokenFromResponse(String responseBody) {
        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(responseBody, Map.class);
        return map.get("access_token");
    }
}
