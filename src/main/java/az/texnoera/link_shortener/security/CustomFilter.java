package az.texnoera.link_shortener.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomFilter extends OncePerRequestFilter {
    private final JWTUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    String authorization = request.getHeader("Authorization");
    String jwtToken ="";
    if (authorization != null && authorization.startsWith("Bearer ")) {
        jwtToken= authorization.substring(7);
        if (jwtUtils.validateJwtToken(jwtToken)) {
            Claims claims = jwtUtils.parseJwtToken(jwtToken);
            String email = claims.getSubject();
            UserDetails user = userDetailsService.loadUserByUsername(email);
            Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
        filterChain.doFilter(request, response);
    }
}
