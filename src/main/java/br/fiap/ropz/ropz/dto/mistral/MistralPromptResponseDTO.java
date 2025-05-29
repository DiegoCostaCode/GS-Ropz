package br.fiap.ropz.ropz.dto.mistral;

import br.fiap.ropz.ropz.model.relatorio.EnumRisco;

public record MistralPromptResponseDTO(
        Long relatorioTemperatura,
        EnumRisco nivelRisco,
        String mensagem
) { }
