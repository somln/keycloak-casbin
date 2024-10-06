package folletto.toyproject.global.casbin;

import folletto.toyproject.global.dto.ResponseDto;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RolesAllowed("USER")
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

    //그룹 권한 부여
    @PostMapping("/api/authorization/groups/{groupId}")
    public ResponseDto<Void> addGroupPolicy(
            @PathVariable Long groupId,
            @RequestBody PolicyRequest request
    ) {
        authorizationManager.addGroupPolicies(groupId, request.policies());
        return ResponseDto.created();
    }

    //유저 권한 부여
    @PostMapping("/api/authorization/users/{userId}")
    public ResponseDto<Void> addUserPolicy(
            @PathVariable Long userId,
            @RequestBody PolicyRequest request
    ) {
        authorizationManager.addUserPolicies(userId, request.policies());
        return ResponseDto.created();
    }

    //그룹 권한 삭제
    @DeleteMapping("/api/authorization/groups/{groupId}")
    public ResponseDto<Void> deleteGroupPolicy(
            @PathVariable Long groupId,
            @RequestBody PolicyRequest request
    ) {
        authorizationManager.deleteGroupPolicies(groupId, request.policies());
        return ResponseDto.ok();
    }

    //유저 권한 삭제
    @DeleteMapping("/api/authorization/users/{userId}")
    public ResponseDto<Void> deleteUserPolicy(
            @PathVariable Long userId,
            @RequestBody PolicyRequest request
    ) {
        authorizationManager.deleteUserPolicies(userId, request.policies());
        return ResponseDto.ok();
    }

    //그룹 권한 조회
    @GetMapping("/api/authorization/groups/{groupId}")
    public ResponseDto<List<List<String>>> getGroupPolicy(
            @PathVariable Long groupId
    ) {
        return ResponseDto.okWithData(authorizationManager.getGroupPolicies(groupId));
    }

    //유저 권한 조회
    @GetMapping("/api/authorization/users/{userId}")
    public ResponseDto<List<List<String>>> getUserPolicy(
            @PathVariable Long userId
    ) {
        return ResponseDto.okWithData(authorizationManager.getUserPolicies(userId));
    }

}