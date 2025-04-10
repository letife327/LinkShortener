package az.texnoera.link_shortener.weatherApi;

import lombok.Data;

@Data
public class WeatherRequest {
    private double latitude;
    private double longitude;
    private double generationtime_ms;
    private int utc_offset_seconds;
    private String timezone;
    private String timezone_abbreviation;
    private double elevation;
    private CurrentUnits current_units;
    private Current current;

    @Data
    public static class CurrentUnits {
        private String time;
        private String interval;
        private String temperature_2m;
        private String rain;
        private String snowfall;
        private String wind_speed_10m;
        private String relative_humidity_2m;
        private String is_day;
    }

    @Data
    public static class Current {
        private String time;
        private int interval;
        private double temperature_2m;
        private double rain;
        private double snowfall;
        private double wind_speed_10m;
        private int relative_humidity_2m;
        private int is_day;
    }
}
