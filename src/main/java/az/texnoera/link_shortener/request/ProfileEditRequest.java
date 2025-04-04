package az.texnoera.link_shortener.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditRequest {
    @NotBlank(message = "Full name cannot be empty")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",
            message = "Full name can only contain letters and must not include numbers or symbols"
    )
    private String fullName;
}
