package az.texnoera.link_shortener.weatherApi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/weather")
@RestController
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;
    @GetMapping("/baku")
    public WeatherRequest getBaku() {
        return weatherService.getWeather();
    }
}
