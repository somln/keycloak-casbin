package folletto.toyproject.auth.dto;

import folletto.toyproject.auth.UserRole;

import javax.validation.constraints.NotNull;

public record UserDto(
        String email,
        String password,
        @NotNull
        UserRole userRole
) {
}
