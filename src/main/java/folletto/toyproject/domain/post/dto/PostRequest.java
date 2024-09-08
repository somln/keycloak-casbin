package folletto.toyproject.domain.post.dto;

import javax.validation.constraints.NotBlank;

public record PostRequest(
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        String title,

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        String content
) {
}
