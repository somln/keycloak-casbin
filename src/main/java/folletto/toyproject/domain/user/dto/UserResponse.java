package folletto.toyproject.domain.user.dto;

import folletto.toyproject.domain.user.entity.UserEntity;

public record UserResponse(
        Long userId,
        String username,
        String email,
        String name,
        boolean isMasterUser
) {
    public static UserResponse from(UserEntity user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.isMasterUser()
        );
    }
}
