package az.texnoera.link_shortener.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResult {
    private String message;
    private int code;
}
