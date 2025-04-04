package az.texnoera.link_shortener.result;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
public class ValidationErrorResult {
    private final String message;
    private final int code;
    private final List<String> errors;
}
