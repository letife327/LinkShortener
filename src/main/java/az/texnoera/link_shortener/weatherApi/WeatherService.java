package az.texnoera.link_shortener.weatherApi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherClient weatherClient;

    public WeatherRequest getWeather() {
        return weatherClient.getForecast(
                40.3777, // latitude of Baku
                          49.892,
                "temperature_2m,rain,snowfall,wind_speed_10m,relative_humidity_2m,is_day"
        );
    }
}
