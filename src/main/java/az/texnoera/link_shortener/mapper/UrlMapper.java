package az.texnoera.link_shortener.mapper;

import az.texnoera.link_shortener.entity.Url;
import az.texnoera.link_shortener.request.UrlRequest;
import az.texnoera.link_shortener.response.UrlResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class UrlMapper {

    public static List<UrlResponse> entityToResponse(Page<Url> pageUrl) {
        return pageUrl.stream().map(url -> {
            return UrlResponse.builder()
                    .title(url.getTitle())
                    .originalUrl(url.getUrl())
                    .shortenedUrl(url.getShortCode())
                    .build();
        }).toList();
    }

}
