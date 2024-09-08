package folletto.toyproject.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 username입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 email입니다."),
    SESSION_INVALID(HttpStatus.UNAUTHORIZED, "세션이 유효하지 않거나 만료되었습니다"),
    SIGNUP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입 중 오류가 발생했습니다. 다시 시도해 주세요.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}