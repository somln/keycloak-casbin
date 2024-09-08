package folletto.toyproject.domain.comment.dto;

import javax.validation.constraints.NotBlank;

public record CommentRequest (
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        String content
){

}
