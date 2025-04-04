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
    @Pattern(
            regexp = "^[A-Za-z]+$",
            message = "Title can only contain uppercase and lowercase letters"
    )
    private String title;
    @NotBlank(message = "URL cannot be empty")
    @Pattern(
            regexp = "^(https?:\\/\\/)?([\\w.-]+)\\.([a-z]{2,6})([\\/\\w .-]*)*\\/?$",
            message = "Please enter a valid URL"
    )
    private String originalUrl;

}
