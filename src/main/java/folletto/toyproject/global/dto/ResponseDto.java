package folletto.toyproject.global.dto;

import folletto.toyproject.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseDto<T> {

    private final int code;
    private final T data;
    private final String errorMessage;

    @Builder
    private ResponseDto(int code, T data, String errorMessage) {
        this.code = code;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static ResponseDto<Void> ok() {
        return ResponseDto.<Void>builder()
                .code(HttpStatus.OK.value())
                .build();
    }

    public static <T> ResponseDto<T> okWithData(T data) {
        return ResponseDto.<T>builder()
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    public static ResponseDto<Void> created() {
        return ResponseDto.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .build();
    }

    public static <T> ResponseDto<T> createdWithData(T data) {
        return ResponseDto.<T>builder()
                .code(HttpStatus.CREATED.value())
                .data(data)
                .build();
    }

    public static ResponseDto<Void> error(ErrorCode errorCode) {
        return ResponseDto.<Void>builder()
                .code(errorCode.getHttpStatus().value())
                .errorMessage(errorCode.getMessage())
                .build();
    }

    public static ResponseDto<Void> errorWithMessage(HttpStatus httpStatus, String errorMessage) {
        return ResponseDto.<Void>builder()
                .code(httpStatus.value())
                .errorMessage(errorMessage)
                .build();
    }
}