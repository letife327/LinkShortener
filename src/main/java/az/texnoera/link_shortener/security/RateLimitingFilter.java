package az.texnoera.link_shortener.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.filter.OncePerRequestFilter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {
    private final JedisPool jedisPool;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if(path.equals("/{short-code}")
                || path.equals("/profile/download/{file-name}")
                || path.equals("/profile/uploadPhoto")
                || path.equals("/v1/users/url-list/{user-id}")
                || path.equals("/v1/users/{id}") || path.equals("/api/weather/baku")){
            filterChain.doFilter(request, response);
            return;
        }
        String key  = "rate limit:"+path;
        try(Jedis jedis = jedisPool.getResource()) {
            String requestCount = jedis.get(key);
            int count = requestCount!=null?Integer.parseInt(requestCount):0;
                if (count <5) {
                    jedis.incr(key);
                    jedis.expire(key,60);
                }else{
                    response.setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG);
                    response.getWriter().write("Rate limit exceeded. Try again later.");
                }

        }
        filterChain.doFilter(request, response);
    }
}
