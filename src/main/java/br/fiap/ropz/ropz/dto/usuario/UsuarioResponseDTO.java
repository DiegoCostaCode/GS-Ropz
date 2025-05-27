package br.fiap.ropz.ropz.dto.usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String cep,
        String dataCadastro
){
}
