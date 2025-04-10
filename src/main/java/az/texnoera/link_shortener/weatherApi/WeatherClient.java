package az.texnoera.link_shortener.weatherApi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(name = "weatherClient", url = "https://api.open-meteo.com/v1")
public interface WeatherClient {
    @GetMapping(value = "/forecast", consumes = "application/json")
    WeatherRequest getForecast(@RequestParam("latitude") double latitude,
                                @RequestParam("longitude") double longitude,
                                @RequestParam("current") String currentFields);

}
