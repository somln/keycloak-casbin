package folletto.toyproject.domain.user.api;

import folletto.toyproject.domain.user.dto.SignupRequest;
import folletto.toyproject.domain.user.dto.UserResponse;
import folletto.toyproject.domain.user.service.UserService;
import folletto.toyproject.global.dto.ResponseDto;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseDto<Void> signup(@RequestBody @Valid SignupRequest signupDto) {
        userService.signUp(signupDto);
        return ResponseDto.created();
    }

    @GetMapping("/users/me")
    @RolesAllowed({"USER"})
    public ResponseDto<UserResponse> findMyInfo() {
        return ResponseDto.okWithData(userService.findMyInfo());
    }

    @GetMapping("/groups/{groupId}/users")
    @RolesAllowed("USER")
    public ResponseDto<List<UserResponse>> findUsersByGroupId(@PathVariable Long groupId) {
        return ResponseDto.okWithData(userService.findUserByGroup(groupId));
    }

    @GetMapping("/{userId}")
    @RolesAllowed("USER")
    public ResponseDto<UserResponse> findUserById(@PathVariable Long userId) {
        return ResponseDto.okWithData(userService.findUser(userId));
    }

    @PatchMapping ("/{userId}/groups/{groupId}/set-master")
    public ResponseDto<Void> setMasterUser(@PathVariable Long userId, @PathVariable Long groupId) {
        userService.setMasterUser(userId, groupId);
        return ResponseDto.ok();
    }

}
