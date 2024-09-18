package folletto.toyproject.global.exception;

import folletto.toyproject.global.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class AuthExceptionRestAdvice {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDto<Void>> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDto.errorWithMessage(HttpStatus.UNAUTHORIZED, "Authentication failed: " + e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDto<Void>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ResponseDto.errorWithMessage(HttpStatus.FORBIDDEN,
                        "Access denied: " + e.getMessage()));
    }

}
