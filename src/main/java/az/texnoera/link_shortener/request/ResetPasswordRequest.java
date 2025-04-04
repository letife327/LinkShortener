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
public class ResetPasswordRequest {
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please enter a valid email address")
    @Size(max=100, message = "Email must be at most 100 characters long")
    private String email;
    @NotBlank(message = "OTP cannot be empty")
    @Pattern(
            regexp = "^[0-9]{6}$",
            message = "OTP must be exactly 6 digits"
    )
    private Integer otp;
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&+])[A-Za-z\\d@$!%*?&+]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;
}
