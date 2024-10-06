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

    @GetMapping("users/{userId}")
    @RolesAllowed("USER")
    public ResponseDto<UserResponse> findUserById(@PathVariable Long userId) {
        return ResponseDto.okWithData(userService.findUser(userId));
    }

    @PatchMapping ("/users/{userId}/groups/{groupId}/set-master")
    @RolesAllowed("USER")
    public ResponseDto<Void> setMasterUser(@PathVariable Long userId, @PathVariable Long groupId) {
        userService.setMasterUser(userId, groupId);
        return ResponseDto.ok();
    }

    @PatchMapping("/users/{userId}/groups/{groupId}/unset-master")
    @RolesAllowed("USER")
    public ResponseDto<Void> unsetMasterUser(@PathVariable Long userId, @PathVariable Long groupId) {
        userService.unsetMasterUser(userId, groupId);
        return ResponseDto.ok();
    }

    @GetMapping("/groups/{groupId}/users/master")
    @RolesAllowed("USER")
    public ResponseDto<List<UserResponse>> findMasterUser(@PathVariable Long groupId) {
        return ResponseDto.okWithData(userService.findMasterUsers(groupId));
    }

}
