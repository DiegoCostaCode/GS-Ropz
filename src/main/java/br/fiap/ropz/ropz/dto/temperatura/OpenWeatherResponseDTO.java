package br.fiap.ropz.ropz.dto.temperatura;

import java.util.List;

public record OpenWeatherResponseDTO(
        Long dt,
        Main main,
        List<Weather> weather
) {
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