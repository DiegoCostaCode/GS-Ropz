package br.fiap.ropz.ropz.model.usuario;

import lombok.Getter;

@Getter
public enum Enum_tipo_usuario {
    ADM("adm"),
    DEFAULT("default");

    private final String descricao;

    Enum_tipo_usuario(String descricao) {
        this.descricao = descricao;
    }
}
