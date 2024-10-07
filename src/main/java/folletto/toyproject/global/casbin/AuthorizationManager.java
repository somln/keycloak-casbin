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

        if (!enforcer.enforce(user.getUsername(), obj.name(), act.name(), group.getGroupName())) {  // 해당 권한이 없을 경우
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        return user;
    }

    public void addGroupPolicies(Long groupId, List<Policy> policies) {
        GroupEntity group = findGroupById(groupId);
        verify(ROLE, CREATE, group.getGroupId());

        // 그룹의 마스터 유저 목록 가져오기
        List<UserEntity> masterUsers = userRepository.findByGroupIdAndIsMasterUser(groupId, true);

        // 그룹에 정책 추가
        policies.forEach(policy -> {
            // 그룹에 대한 정책 추가
            addPolicyToEnforcer(group.getGroupName(), policy, group.getGroupName());

            // 각 마스터 유저에 대해 동일한 정책 추가
            masterUsers.forEach(masterUser -> {
                addPolicyToEnforcer(masterUser.getUsername(), policy, group.getGroupName());
            });
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

        // 해당 그룹에 속한 마스터 유저 목록 가져오기
        List<UserEntity> masterUsers = userRepository.findByGroupIdAndIsMasterUser(group.getGroupId(), true);

        // 그룹에 대한 정책 삭제
        policies.forEach(policy -> {
            deletePolicyToEnforcer(group.getGroupName(), policy, group.getGroupName());
        });

        // 마스터 유저에 대한 정책 삭제
        masterUsers.forEach(masterUser -> {
            policies.forEach(policy -> {
                deletePolicyToEnforcer(masterUser.getUsername(), policy, group.getGroupName());
            });
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

    public void addAdminUserPolicies(Long userId, Long groupId) {

        GroupEntity group = findGroupById(groupId);
        UserEntity user = findUserById(userId);

        // 그룹에 해당하는 모든 정책 가져오기 (sub 기준으로 필터링)
        List<List<String>> filteredPolicies = enforcer.getFilteredPolicy(0, group.getGroupName());

        // 각 정책을 user에게 복사하여 할당
        for (List<String> policy : filteredPolicies) {
            String obj = policy.get(1);  // 객체 (예: BOARD)
            String act = policy.get(2);  // 액션 (예: CREATE)
            String dom = policy.get(3);  // 도메인 (예: group.getGroupName())

            // 사용자에게 해당 정책 추가 (user.getUsername()을 기준으로)
            enforcer.addPolicy(user.getUsername(), obj, act, dom);
        }

        // 정책을 영구적으로 저장
        enforcer.savePolicy();
    }
}
