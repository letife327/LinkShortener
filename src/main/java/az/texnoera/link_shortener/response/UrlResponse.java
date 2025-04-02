package az.texnoera.link_shortener.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UrlResponse {
    private String title;
    private String originalUrl;
    private String shortenedUrl;

}
