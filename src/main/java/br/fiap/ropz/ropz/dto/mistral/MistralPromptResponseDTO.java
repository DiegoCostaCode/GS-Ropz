package br.fiap.ropz.ropz.dto.mistral;

import br.fiap.ropz.ropz.model.relatorio.EnumRisco;

public record MistralPromptResponseDTO(
        EnumRisco nivelRisco,
        String mensagem
) { }
