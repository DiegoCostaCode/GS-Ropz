package br.fiap.ropz.ropz.dto.localizacao;

public record LocalizacaoResponseDTO(
        Long id,
        String cep,
        String bairro,
        String cidade,
        String estado,
        Double latitude,
        Double longitude
) {
}
