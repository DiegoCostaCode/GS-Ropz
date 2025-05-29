package br.fiap.ropz.ropz.model.usuario;

import lombok.Getter;

@Getter
public enum EnumTipoUsuario {
    ADM("adm"),
    DEFAULT("default");

    private final String descricao;

    EnumTipoUsuario(String descricao) {
        this.descricao = descricao;
    }
}
