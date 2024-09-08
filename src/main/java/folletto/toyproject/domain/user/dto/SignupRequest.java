package folletto.toyproject.domain.user.dto;

import folletto.toyproject.domain.user.entity.UserEntity;

public record SignupRequest(
        String username,
        String email,
        String name,
        String password

){
    public UserEntity toEntity(String userUUID) {
        return UserEntity.builder()
                .userUUID(userUUID)
                .username(username)
                .email(email)
                .name(name)
                .build();
    }
}
