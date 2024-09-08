package folletto.toyproject.global.dto;

import javax.validation.constraints.NotBlank;

public record SearchRequest(
        @NotBlank(message = "검색어를 입력해주세요")
        String q
) {

}
