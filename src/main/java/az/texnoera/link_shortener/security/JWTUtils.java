package az.texnoera.link_shortener.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JWTUtils {
    private String jwtSecret = "adsiaosidfjioweejfiowjeiofjewiojfwieasdkaskpofkcpowdcdiwejeiufchjfiuerwjiucjnerinviuneriowjvwpovs[oka[jv[s";

    public Key getKey() {
        if (jwtSecret.getBytes().length < 32) {
            throw new RuntimeException("JWT secret length less than 32");
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateJwtToken(String email, List<String> roles) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600 * 1000);
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .claim("roles", roles)
                .signWith(getKey(), SignatureAlgorithm.HS512);

        return jwtBuilder.compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public Claims parseJwtToken(String authToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(authToken)
                .getBody();
    }
}
