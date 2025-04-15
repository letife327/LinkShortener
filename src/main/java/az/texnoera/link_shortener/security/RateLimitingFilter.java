package az.texnoera.link_shortener.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.filter.OncePerRequestFilter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {
    private final JedisPool jedisPool;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String clientIP = request.getRemoteAddr();
        String path = request.getRequestURI();
        System.out.println(path);
        System.out.println(clientIP);
        if(path.equals("/{short-code}")
                || path.equals("/profile/download/{file-name}")
                || path.equals("/profile/uploadPhoto")
                || path.equals("/v1/users/url-list/{user-id}")
                || path.equals("/v1/users/{id}") || path.equals("/api/weather/baku")){
            filterChain.doFilter(request, response);
            return;
        }
        String key  = "rate limit:"+clientIP+path;
        try(Jedis jedis = jedisPool.getResource()) {
            String requestCount = jedis.get(key);
            int count = requestCount!=null?Integer.parseInt(requestCount):0;
                if (count <20) {
                    jedis.incr(key);
                    jedis.expire(key,60);
                }else{
                    log.error("Rate limited");
                    response.setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG);
                    response.setContentType("application/json");
                    response.getWriter().write("""
                {
                    "error": "Rate limit exceeded",
                    "message": "You can only make 5 requests per minute to this endpoint."
                }
                """);
                    return;
                }
        }
        filterChain.doFilter(request, response);
    }
}
