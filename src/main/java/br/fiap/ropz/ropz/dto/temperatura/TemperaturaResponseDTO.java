package br.fiap.ropz.ropz.dto.temperatura;

import br.fiap.ropz.ropz.dto.localizacao.LocalizacaoResponseDTO;

public record TemperaturaResponseDTO(
        Long id,
        String icon,
        String tempo,
        String descricao,
        Double temperatura,
        Double temperaturaMaxima,
        Double temperaturaMinima,
        Double sensacaoTermica,
        Integer umidade,
        LocalizacaoResponseDTO localizacao,
        String dataHora
) {
}
