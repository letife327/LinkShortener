package az.texnoera.link_shortener.util;

import org.springframework.stereotype.Component;

@Component
public class UrlUtil {
    private final String symbols = "h1yWp9SAaetLMEqTKF3jZYVIblJ2XPwHvzmDNs74nfd0BuQcOkio5CR68GUgxr";

    public String generateShortCode(long counter) {
        StringBuilder sb = new StringBuilder();
        while (counter > 0) {
            int index = (int) (counter % 62);
            sb.append(symbols.charAt(index));
            counter = counter / 62;
        }
        return sb.reverse().toString();
    }
}
