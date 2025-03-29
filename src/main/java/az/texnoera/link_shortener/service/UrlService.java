package az.texnoera.link_shortener.service;

import az.texnoera.link_shortener.entity.Url;
import az.texnoera.link_shortener.entity.User;
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
    private static final String URL_PREFIX = "url:";
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
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
                .redisCounter(0)
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
        String redisUrl = redisTemplate.opsForValue().get(URL_PREFIX+shortCode);

        if(redisUrl != null) {
            response.setStatus(HttpStatus.FOUND.value());
            response.addHeader("Location", redisUrl);
            incrementVisitCountInRedis(shortCode);
            System.out.println(redisUrl);
            return;
        }

        Url url = urlRepository.findByShortCode(shortCode);
        if (url.getUrl() == null) {
            throw new RuntimeException("url not found");
        }
        response.setStatus(HttpStatus.FOUND.value());
        response.addHeader("Location", url.getUrl());

        incrementVisitCountDB(shortCode);

        Integer visitCount  = getVisitCount(shortCode);
        if (visitCount>=5){
            redisTemplate.opsForValue().set(URL_PREFIX + shortCode, url.getUrl());
            redisTemplate.opsForValue().set("visit_count:" + shortCode, String.valueOf(visitCount));
        }
    }

    private void incrementVisitCountInRedis(String shortCode) {
        String redisVisitCountKey = "visit_count:" + urlRepository.findByShortCode(shortCode).getRedisCounter();
        redisTemplate.opsForValue().increment(redisVisitCountKey, 5);
        System.out.println(redisVisitCountKey);
    }

    private void incrementVisitCountDB(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode);
        if(url != null) {
            url.setRedisCounter((url.getRedisCounter() + 1));
            url.setLastVisitTime(LocalDateTime.now());
            urlRepository.save(url);
        }
    }

    public Integer getVisitCount(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode);
        if (url != null) {
            return url.getRedisCounter();
        }
        return 0;
    }

    @Scheduled(fixedRate = 86400000)
    public void deleteExpireUrl() {
       List<Url> urls = urlRepository.findAllLinksWithUsers();
        urls.stream()
                .filter(url -> {
                    return url.getExpireTime().isBefore(LocalDateTime.now());
                }).forEach(urlRepository::delete);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpOldUrlsFromRedis(){
        LocalDateTime now = LocalDateTime.now();
        List<Url> urls = urlRepository.findByLastVisitTimeBefore(now.minusDays(3));
        urls.forEach(url -> {
                    redisTemplate.delete(URL_PREFIX + url.getShortCode());
                });
    }
}
