package folletto.toyproject.auth;

import com.google.gson.Gson;
import folletto.toyproject.auth.dto.SignupRequestDto;
import folletto.toyproject.auth.dto.TokenResponseDto;
import folletto.toyproject.http.HttpClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/api")
@RestController
public class AuthController {

    @Value("${custom-keycloak.grant_type}")
    private String grantType;

    @Value("${custom-keycloak.client_id}")
    private String clientId;

    @Value("${custom-keycloak.client_secret}")
    private String clientSecret;

    @Value("${custom-keycloak.username}")
    private String username;

    @Value("${custom-keycloak.password}")
    private String password;

    @Value("${custom-keycloak.token_url}")
    private String tokenUrl;

    private static final String hostUrl = "http://localhost:8180";
    private final HttpClient httpClient = new HttpClient();

    @PostMapping("/signup")
    public Response signup(@RequestBody SignupRequestDto signupDto) throws IOException {
        return httpClient.post(hostUrl + "/admin/realms/board/users", getToken(), signupDto
        );
    }

    @PostMapping("/getToken")
    public String getToken() throws IOException {
        String url = tokenUrl;

        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", grantType);
        formParams.put("client_id", clientId);
        formParams.put("client_secret", clientSecret);
        formParams.put("username", username);
        formParams.put("password", password);

        String responseBody = httpClient.postForm(url, formParams).body().string();
        return extractTokenFromResponse(responseBody);
    }

    private String extractTokenFromResponse(String responseBody) {
        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(responseBody, Map.class);
        return map.get("access_token");
    }

}
