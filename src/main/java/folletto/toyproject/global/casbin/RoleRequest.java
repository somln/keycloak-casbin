package folletto.toyproject.global.casbin;

import java.util.List;

public record RoleRequest(
        List<AddRoleRequest> roles
) {

}
