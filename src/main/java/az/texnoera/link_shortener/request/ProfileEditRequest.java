package az.texnoera.link_shortener.request;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditRequest {
    private String fullName;
}
