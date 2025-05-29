package br.fiap.ropz.ropz.dto.temperatura;

import java.util.List;

public record OpenWeatherForecastResponseDTO(
        List<ForecastEntry> list
) {

    public record ForecastEntry(
            String dt_txt,
            Main main,
            List<Weather> weather
    ) {}

    public record Main(
            Double temp,
            Double feels_like,
            Double temp_min,
            Double temp_max,
            Integer humidity
    ) {}

    public record Weather(
            String main,
            String description,
            String icon
    ) {}

}

