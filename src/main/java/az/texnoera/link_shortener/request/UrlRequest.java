package az.texnoera.link_shortener.request;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlRequest {
    private String originalUrl;
}
