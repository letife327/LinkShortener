package az.texnoera.link_shortener.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerificationRequest {
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please enter a valid email address")
    @Size(max=100, message = "Email must be at most 100 characters long")
    private String email;
    private Integer otp;
}
