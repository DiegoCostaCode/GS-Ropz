package br.fiap.ropz.ropz.dto.relatorio;

import br.fiap.ropz.ropz.model.relatorio.Relatorio;

import java.util.List;

public record RelatoriosServiceDTO(
        List<Relatorio> relatoriosCurrent,
        Relatorio relatorioForecast,
        Relatorio relatorioMaisRecente
) { }