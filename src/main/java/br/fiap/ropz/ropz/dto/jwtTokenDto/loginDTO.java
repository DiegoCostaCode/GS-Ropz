package br.fiap.ropz.ropz.dto.jwtTokenDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record loginDTO(
        @Email(message = "Email inválido")
        String email,
        @NotBlank(message = "Senha não pode ser vazia")
        String senha
) { }
