package folletto.toyproject.domain.user.api;

import folletto.toyproject.domain.user.dto.SignupRequest;
import folletto.toyproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public void signup(@RequestBody SignupRequest signupDto)  {
        userService.signUp(signupDto);
    }

}
