package az.texnoera.link_shortener.adviser;

import az.texnoera.link_shortener.exception.BaseException;
import az.texnoera.link_shortener.result.ErrorResult;
import az.texnoera.link_shortener.result.ValidationErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResult>  handleBaseException(BaseException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResult(ex.getMessage(),ex.getStatusCode().getStatusCode()));
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResult>  handleRuntimeException(RuntimeException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResult("BAD REQUEST",4000));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResult>  handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        log.error(ex.getMessage());
        List<String> errors = new ArrayList<>();
        ex.getAllErrors().forEach(error-> errors.add(error.getDefaultMessage()));
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ValidationErrorResult("Field is not valid",1010,errors));
}
}
