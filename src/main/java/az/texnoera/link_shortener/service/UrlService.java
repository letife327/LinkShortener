package az.texnoera.link_shortener.service;

import az.texnoera.link_shortener.entity.Url;
import az.texnoera.link_shortener.entity.User;
import az.texnoera.link_shortener.repository.UrlRepository;
import az.texnoera.link_shortener.repository.UserRepository;
import az.texnoera.link_shortener.request.UrlRequest;
import az.texnoera.link_shortener.response.ShortenerUrlResponse;
import az.texnoera.link_shortener.security.SecurityUtils;
import az.texnoera.link_shortener.util.UrlUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
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
                .expireTime(expirationDate)
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
            throw new RuntimeException("url not found");
        }
        response.setStatus(HttpStatus.FOUND.value());
        response.addHeader("Location", url.getUrl());
    }
}
