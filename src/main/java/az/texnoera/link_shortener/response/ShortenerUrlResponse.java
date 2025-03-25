package az.texnoera.link_shortener.response;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortenerUrlResponse {
    private String shortUrl;
}
