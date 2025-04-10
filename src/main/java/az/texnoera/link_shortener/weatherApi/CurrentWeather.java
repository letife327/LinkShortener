package az.texnoera.link_shortener.weatherApi;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CurrentWeather {
    private double temperature_2m;
    private double rain;
    private double snowfall;
    private double wind_speed_10m;
    private double relative_humidity_2m;
    private int is_day;
}
