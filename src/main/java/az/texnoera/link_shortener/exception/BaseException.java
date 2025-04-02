package az.texnoera.link_shortener.exception;

import az.texnoera.link_shortener.enums.StatusCodeForException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException{
    private final HttpStatus status;
    private final StatusCodeForException statusCode;
    public BaseException( HttpStatus status, StatusCodeForException statusCode) {
        super(statusCode.getMessage());
        this.status = status;
        this.statusCode = statusCode;
    }
}
