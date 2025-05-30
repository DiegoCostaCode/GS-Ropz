package br.fiap.ropz.ropz.dto.relatorio;

import br.fiap.ropz.ropz.dto.temperatura.TemperaturaResponseDTO;

public record RelatorioResponseDTO(
        Long id,
        String risco,
        String mensagem,
        TemperaturaResponseDTO temperatura,
        String dataHora,
        String dataCriacao
) {
}
