package az.texnoera.link_shortener.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {
    private final CustomFilter customFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("v1/auth/**").permitAll()
                                .requestMatchers("v1/auth/verifying-code").permitAll()
                                .requestMatchers("v1/auth/login").permitAll()
                                .requestMatchers("api/weather/baku").permitAll()
                                .requestMatchers("/{short-code:[a-zA-Z0-9]{5,}}").permitAll()
                                .requestMatchers("/profile/download/{file-name}").permitAll()
                                .requestMatchers(permitSwagger).permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                );
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500","https://s.shamilabilov.social")); // Frontend’in çalıştığı domaini ekle
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Tüm uç noktalar için geçerli
                .allowedOrigins("http://127.0.0.1:5500") // Bu kaynağa izin ver
                .allowedMethods("GET", "POST", "PUT", "DELETE") // İzin verilen HTTP metodları
                .allowedHeaders("*")  // İzin verilen başlıklar
                .allowCredentials(true);  // Kimlik doğrulama bilgilerini (cookie, authorization) kabul et
    }

    private final String[] permitSwagger = {
            "/v3/api-docs/**",
            "/v3/api-docs.yanl",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
}
