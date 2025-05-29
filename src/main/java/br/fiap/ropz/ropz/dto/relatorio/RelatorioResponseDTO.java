package br.fiap.ropz.ropz.dto.relatorio;

public record RelatorioResponseDTO(
        Long id,
        String risco,
        String mensagem,
        Long temperaturaId,
        String temperaturaIcon,
        String dataHora,
        String dataCriacao
) {
}
