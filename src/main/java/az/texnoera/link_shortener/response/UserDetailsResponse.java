package az.texnoera.link_shortener.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDetailsResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String photoUrl;
}
