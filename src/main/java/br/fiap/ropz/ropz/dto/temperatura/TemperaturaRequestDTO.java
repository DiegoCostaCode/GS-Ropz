package br.fiap.ropz.ropz.dto.temperatura;

import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.temperatura.EnumOrigem;

import java.time.LocalDateTime;

public record TemperaturaRequestDTO (
        String icon,
        String tempo,
        String descricao,
        Double valorCelsius,
        Double maxValorCelsius,
        Double minValorCelsius,
        Double sensacaoTermica,
        Integer umidade,
        LocalDateTime criadoEm,
        LocalDateTime dataHora,
        Localizacao localizacao,
        EnumOrigem tipoConsulta
){ }
