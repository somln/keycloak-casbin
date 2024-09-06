package folletto.toyproject.domain.user.api;

import folletto.toyproject.domain.user.dto.SignupRequestDto;
import folletto.toyproject.global.keycloak.KeyCloakClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/api/users")
@RestController
public class UserController {

    private final KeyCloakClient keyCloakClient;

    @Autowired
    public UserController(KeyCloakClient keyCloakClient) {
        this.keyCloakClient = keyCloakClient;
    }

    @PostMapping("/signup")
    public Response signup(@RequestBody SignupRequestDto signupDto) throws IOException {
        //서버에 저장 후 keycloak에 요청
        return keyCloakClient.signup(signupDto);
    }

}
