package folletto.toyproject.domain.user.dto;

import folletto.toyproject.domain.user.entity.UserEntity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank(message = "사용자 이름은 필수 입력 항목입니다.")
        String username,

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        String email,

        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        String name,

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        String password

) {

    public UserEntity toEntity(String userUUID) {
        return UserEntity.builder()
                .userUUID(userUUID)
                .username(username)
                .email(email)
                .name(name)
                .build();
    }
}
