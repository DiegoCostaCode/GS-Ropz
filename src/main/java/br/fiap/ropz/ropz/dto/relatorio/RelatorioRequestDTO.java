package br.fiap.ropz.ropz.dto.relatorio;

import br.fiap.ropz.ropz.model.relatorio.EnumRisco;

public record RelatorioRequestDTO(

        EnumRisco risco,
        String mensagem,
        Long relatorioTemperatura
) {
}
