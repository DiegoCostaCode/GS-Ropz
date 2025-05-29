package br.fiap.ropz.ropz.model.temperatura;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumOrigem {
    CURRENT("OpenWeatherCurrent"),
    FORECAST("OpenWeatherForecast");

    private final String descricao;
}
