package br.fiap.ropz.ropz.dto.jwtTokenDto;

import br.fiap.ropz.ropz.dto.usuario.UsuarioResponseDTO;

public record ValidUserDTO(
        UsuarioResponseDTO usuario,
        String token
) {
}
