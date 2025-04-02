package az.texnoera.link_shortener.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UserResponse {
    private Integer id;
    private String token;
    private String fullName;
    private String email;
}
