package az.texnoera.link_shortener.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlRequest {
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 20, message = "Title must be at most 20 characters long")
    private String title;
    @NotBlank(message = "URL cannot be empty")
    private String originalUrl;

}
