package folletto.toyproject.auth;

import folletto.toyproject.auth.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class AuthController {

    private final AuthService authService;

    /*
     * 회원가입
     * */
    @PostMapping("/signup")
    public ResponseEntity registerUser(@RequestBody UserDto userDto) {

        if (authService.existsByUsername(userDto.email())) {
            return ResponseEntity.ok("유저가 존재합니다.");
        }

        UserDto result = authService.createUser(userDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    /*
     *  로그인
     * */
    @PostMapping(path = "/signin")
    public ResponseEntity<AccessTokenResponse> authenticateUser(@RequestBody UserDto userDto) {

        AccessTokenResponse response = authService.setAuth(userDto);
        return ResponseEntity.ok(response);
    }

}