package az.texnoera.link_shortener.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UserResponse {
    private String token;
    private String fullName;
}
