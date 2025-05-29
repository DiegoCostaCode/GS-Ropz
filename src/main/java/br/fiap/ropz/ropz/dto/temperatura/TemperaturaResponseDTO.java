package br.fiap.ropz.ropz.dto.temperatura;

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
        Long localizacaoId,
        String dataHora
) {
}
