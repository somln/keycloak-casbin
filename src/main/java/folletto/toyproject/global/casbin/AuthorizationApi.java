package folletto.toyproject.global.casbin;

import folletto.toyproject.global.dto.ResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthorizationApi {

    private final AuthorizationManager authorizationManager;

    //일단 개발용
    @GetMapping("/api/authorization/users/{userId}/roles/{groupId}")
    public ResponseDto<List<String>> getRoles(
            @PathVariable Long userId,
            @PathVariable Long groupId
    ) {
        return ResponseDto.okWithData(authorizationManager.getRoles(userId, groupId));
    }

    @PostMapping("/api/authorization/groups/{groupId}")
    public ResponseDto<Void> addGroupRole(
            @PathVariable Long groupId,
            @RequestBody RoleRequest request
    ) {
        authorizationManager.addGroupPolicies(groupId, request.roles());
        return ResponseDto.created();
    }

    @GetMapping("/api/authorization/groups/{groupId}")
    public ResponseDto<List<List<String>>> getGroupRole(
            @PathVariable Long groupId
    ) {
        return ResponseDto.okWithData(authorizationManager.getGroupPolicies(groupId));
    }

    @PostMapping("/api/authorization/users/{userId}")
    public ResponseDto<Void> addUserRole(
            @PathVariable Long userId,
            @RequestBody RoleRequest request
    ) {
        authorizationManager.addUserPolicies(userId, request.roles());
        return ResponseDto.created();
    }

    @GetMapping("/api/authorization/users/{userId}")
    public ResponseDto<List<List<String>>> getUserRole(
            @PathVariable Long userId
    ) {
        return ResponseDto.okWithData(authorizationManager.getUserPolicies(userId));
    }

}