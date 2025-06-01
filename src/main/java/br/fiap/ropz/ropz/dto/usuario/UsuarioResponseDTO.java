package br.fiap.ropz.ropz.dto.usuario;

import br.fiap.ropz.ropz.dto.localizacao.LocalizacaoResponseDTO;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        LocalizacaoResponseDTO localizacao,
        String dataCadastro
){
}
