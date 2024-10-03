package folletto.toyproject.global.casbin;

import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationApi {

    @PostMapping("/api/authorization")
    public static void assignRole(
            @RequestBody AuthorizationRequest request
    ){

    }
}
