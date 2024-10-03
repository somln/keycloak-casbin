package folletto.toyproject.global.casbin;

import folletto.toyproject.domain.group.entity.GroupEntity;
import folletto.toyproject.domain.group.repository.GroupRepository;
import folletto.toyproject.domain.user.entity.UserEntity;
import folletto.toyproject.domain.user.repository.UserRepository;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.main.Enforcer;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationManager {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final Enforcer enforcer;

    public UserEntity verify(String obj, String act, Long groupId) {
        KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userUUID = principal.getName();
        UserEntity user = findUserByUUID(userUUID);
        GroupEntity group = findGroupById(groupId);

        String sub = user.getUsername();
        String dom = group.getGroupName();
        String masterGroup = findMasterGroup();

        if(enforcer.getRolesForUserInDomain(sub, masterGroup).isEmpty()){  //마스터 그룹 마스터 관리자가 아닐 경우
            if (!enforcer.enforce(sub, obj, act, dom)) {
                throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS);
            }
        }

        return user;
    }

    public void addRole(String username, Long groupId) {
        GroupEntity group = findGroupById(groupId);
        enforcer.addRoleForUserInDomain(username, group.getGroupName(), group.getGroupName());
        enforcer.savePolicy();
    }

    public List<String > getRoles(Long userId, Long groupId) {
        UserEntity user = findUserById(userId);
        GroupEntity group = findGroupById(groupId);
        return enforcer.getRolesForUserInDomain(user.getUsername(), group.getGroupName());
    }

    public void addGroupPolicies(Long groupId, List<AddRoleRequest> roles) {
        GroupEntity group = findGroupById(groupId);
        roles.forEach(role -> enforcer.addPolicy(group.getGroupName(), role.object(), role.act(), group.getGroupName()));
        enforcer.savePolicy();
    }

    public List<List<String>> getGroupPolicies(Long groupId) {
        GroupEntity group = findGroupById(groupId);
        return enforcer.getFilteredPolicy(0, group.getGroupName());
    }

    public void addUserPolicies(Long userId, List<AddRoleRequest> roles) {
        UserEntity user = findUserById(userId);
        GroupEntity group = findGroupById(user.getGroupId());

        if (group.isMasterGroup()) {
            // 마스터 그룹일 경우, 사용자의 요청에 따라 바로 권한을 부여
            roles.forEach(role -> enforcer.addPolicy(user.getUsername(), role.object(), role.act(), role.object()));
        } else {
            // 마스터 그룹이 아닐 경우, 그룹이 해당 권한을 가지고 있는지 먼저 확인
            roles.forEach(role -> {
                if (enforcer.enforce(group.getGroupName(), role.object(), role.act(), group.getGroupName())) {
                    // 그룹이 해당 권한을 가지고 있는 경우에만 사용자에게 권한 부여
                    enforcer.addPolicy(user.getUsername(), role.object(), role.act(), group.getGroupName());
                } else {
                    throw new ApplicationException(ErrorCode.GROUP_DOES_NOT_HAVE_PERMISSION);
                }
            });
        }
        enforcer.savePolicy();
    }

    public List<List<String>> getUserPolicies(Long userId) {
        UserEntity user = findUserById(userId);
        return enforcer.getFilteredPolicy(0, user.getUsername());
    }

    private UserEntity findUserByUUID(String userUUID) {
        return userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    private GroupEntity findGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.GROUP_NOT_FOUND));
    }

    private String findMasterGroup() {
        return groupRepository.findByIsMasterGroup(true).getGroupName();
    }
}
