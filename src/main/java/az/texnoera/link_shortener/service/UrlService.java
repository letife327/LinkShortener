package az.texnoera.link_shortener.service;

import az.texnoera.link_shortener.entity.Url;
import az.texnoera.link_shortener.entity.User;
import az.texnoera.link_shortener.enums.StatusCodeForException;
import az.texnoera.link_shortener.exception.BaseException;
import az.texnoera.link_shortener.repository.UrlRepository;
import az.texnoera.link_shortener.request.UrlRequest;
import az.texnoera.link_shortener.security.SecurityUtils;
import az.texnoera.link_shortener.util.UrlUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlUtil urlUtil;
    @Value("${url.domain-name}")
    private String domainName;

    public String changeUrl(UrlRequest urlRequest) {
        User user = SecurityUtils.getCurrentUser();
        System.out.println(user);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationDate = now.plusDays(30);
        Url url = Url.builder()
                .createTime(now)
                .url(urlRequest.getOriginalUrl())
                .title(urlRequest.getTitle())
                .expireTime(expirationDate)
                .lastVisitTime(LocalDateTime.now())
                .user(user)
                .build();
        Url savedUrl = urlRepository.save(url);
        Long counter = savedUrl.getCounter();
        String shortCode = urlUtil.generateShortCode(counter);
        savedUrl.setShortCode(shortCode);
        urlRepository.save(savedUrl);
        return domainName + shortCode;
    }

    public void getShortenerUrl(String shortCode,
                                HttpServletResponse response) {

        Url url = urlRepository.findByShortCode(shortCode);
        if (url.getUrl() == null) {
            throw new BaseException(HttpStatus.NOT_FOUND, StatusCodeForException.URL_NOT_FOUND);
        }
        response.setStatus(HttpStatus.FOUND.value());
        response.addHeader("Location", url.getUrl());

    }
    @Scheduled(fixedRate = 86400000)
    public void deleteExpireUrl() {
       List<Url> urls = urlRepository.findAllLinksWithUsers();
        urls.stream()
                .filter(url -> {
                    return url.getExpireTime().isBefore(LocalDateTime.now());
                }).forEach(urlRepository::delete);
    }

}
