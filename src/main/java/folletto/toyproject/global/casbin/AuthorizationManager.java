package folletto.toyproject.global.casbin;

import static folletto.toyproject.global.casbin.ActionType.CREATE;
import static folletto.toyproject.global.casbin.ActionType.DELETE;
import static folletto.toyproject.global.casbin.ActionType.READ;
import static folletto.toyproject.global.casbin.ObjectType.GROUP;
import static folletto.toyproject.global.casbin.ObjectType.ROLE;

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

    public UserEntity verify(ObjectType obj, ActionType act, Long groupId) {

        KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userUUID = principal.getName();
        UserEntity user = findUserByUUID(userUUID);
        GroupEntity group = findGroupById(groupId);

        String sub = user.getUsername();
        String dom = group.getGroupName();
        String masterGroup = findMasterGroup();

        if (enforcer.getRolesForUserInDomain(sub, masterGroup).isEmpty()) {  // 마스터 그룹의 마스터 관리자가 아닐 경우 검사
            if (!enforcer.enforce(sub, obj.name(), act.name(), dom)) {  // 해당 권한이 없을 경우
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

    public List<String> getRoles(Long userId, Long groupId) {
        UserEntity user = findUserById(userId);
        GroupEntity group = findGroupById(groupId);
        return enforcer.getRolesForUserInDomain(user.getUsername(), group.getGroupName());
    }

    public void deleteRole(String username, Long groupId) {
        GroupEntity group = findGroupById(groupId);
        enforcer.deleteRoleForUserInDomain(username, group.getGroupName(), group.getGroupName());
        enforcer.savePolicy();
    }

    public void addGroupPolicies(Long groupId, List<Policy> Polices) {
        GroupEntity group = findGroupById(groupId);
        verify(ROLE, CREATE, group.getGroupId());
        Polices.forEach(policy -> {
            addPolicyToEnforcer(group.getGroupName(), policy, group.getGroupName());
        });
        enforcer.savePolicy();
    }

    public void addUserPolicies(Long userId, List<Policy> Polices) {
        UserEntity user = findUserById(userId);
        GroupEntity group = findGroupById(user.getGroupId());
        verify(ROLE, CREATE, group.getGroupId());

        if (group.isMasterGroup()) {
            // 사용자가 속한 그룹이 마스터 그룹일 경우, 모든 그룹에 해당 권한을 부여
            List<GroupEntity> allGroups = groupRepository.findAll();  // 모든 그룹 가져오기
            allGroups.forEach(g -> {
                Polices.forEach(policy -> {
                    addPolicyToEnforcer(user.getUsername(), policy, group.getGroupName());
                });
            });
        } else {
            // 마스터 그룹이 아닐 경우, 그룹이 해당 권한을 가지고 있는지 먼저 확인
            Polices.forEach(policy -> {
                if (groupHasPermission(group, policy)) {
                    addPolicyToEnforcer(user.getUsername(), policy, group.getGroupName());
                } else {
                    throw new ApplicationException(ErrorCode.GROUP_DOES_NOT_HAVE_PERMISSION);
                }
            });
        }
        enforcer.savePolicy();
    }

    private boolean groupHasPermission(GroupEntity group, Policy policy) {
        return enforcer.enforce(
                group.getGroupName(),
                ObjectType.from(policy.object()),
                ActionType.from(policy.act()),
                group.getGroupName()
        );
    }

    private void addPolicyToEnforcer(String subject, Policy policy, String groupName) {
        enforcer.addPolicy(
                subject,
                ObjectType.from(policy.object()),
                ActionType.from(policy.act()),
                groupName
        );
    }

    public void deleteGroupPolicies(Long groupId, List<Policy> policies) {
        GroupEntity group = findGroupById(groupId);
        verify(ROLE, DELETE, group.getGroupId());
        policies.forEach(policy -> {
            deletePolicyToEnforcer(group.getGroupName(), policy, group.getGroupName());
        });
        enforcer.savePolicy();
    }

    public void deleteUserPolicies(Long userId, List<Policy> policies) {
        UserEntity user = findUserById(userId);
        GroupEntity group = findGroupById(user.getGroupId());
        verify(ROLE, DELETE, group.getGroupId());
        policies.forEach(policy -> {
            deletePolicyToEnforcer(user.getUsername(), policy, group.getGroupName());
        });
        enforcer.savePolicy();
    }

    private void deletePolicyToEnforcer(String subject, Policy policy, String groupName) {
        enforcer.removePolicy(
                subject,
                ObjectType.from(policy.object()),
                ActionType.from(policy.act()),
                groupName
        );
    }

    public List<List<String>> getGroupPolicies(Long groupId) {
        GroupEntity group = findGroupById(groupId);
        verify(ROLE, READ, group.getGroupId());
        return enforcer.getFilteredPolicy(0, group.getGroupName());
    }

    public List<List<String>> getUserPolicies(Long userId) {
        UserEntity user = findUserById(userId);
        GroupEntity group = findGroupById(user.getGroupId());
        verify(ROLE, READ, group.getGroupId());
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
