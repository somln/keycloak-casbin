package folletto.toyproject.global.exception;

import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USERNAME_ALREADY_EXISTS(BAD_REQUEST, "이미 존재하는 username입니다."),
    EMAIL_ALREADY_EXISTS(BAD_REQUEST, "이미 존재하는 email입니다."),
    SESSION_INVALID(UNAUTHORIZED, "세션이 유효하지 않거나 만료되었습니다"),
    SIGNUP_FAILED(INTERNAL_SERVER_ERROR, "회원가입 중 오류가 발생했습니다. 다시 시도해 주세요."),
    USER_NOT_FOUND(NOT_FOUND, "사용자를 찾을 수 없습니다." ),
    POST_NOT_FOUND(NOT_FOUND, "게시글을 찾을 수 없습니다." ),
    UNAUTHORIZED_ACCESS(FORBIDDEN, "요청에 대한 권한이 없습니다."),
    INVALID_SORT_TYPE(BAD_REQUEST, "정렬 타입이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}