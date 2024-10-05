package folletto.toyproject.global.casbin;

import java.util.List;

public record PolicyRequest(
        List<Policy> policies
) {

}
