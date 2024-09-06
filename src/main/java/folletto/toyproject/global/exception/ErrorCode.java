package folletto.toyproject.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    SESSION_INVALID(HttpStatus.UNAUTHORIZED, "세션이 유효하지 않거나 만료되었습니다");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}