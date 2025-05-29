package br.fiap.ropz.ropz.model.relatorio;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumRisco {
    BAIXO("Baixo"),
    MODERADO("Moderado"),
    ALTO("Alto"),
    EXTREMO("Extremo");

    private final String descricao;
}
