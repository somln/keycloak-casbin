package folletto.toyproject.global.auth;

import folletto.toyproject.domain.group.entity.GroupEntity;
import folletto.toyproject.domain.group.repository.GroupRepository;
import folletto.toyproject.domain.user.entity.UserEntity;
import folletto.toyproject.domain.user.repository.UserRepository;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.main.Enforcer;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class AuthorizationVerifier {

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

        if (!enforcer.enforce(sub, obj, act, dom)) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        return user;
    }

    private UserEntity findUserByUUID(String userUUID) {
        return userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    private GroupEntity findGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.GROUP_NOT_FOUND));
    }

}
