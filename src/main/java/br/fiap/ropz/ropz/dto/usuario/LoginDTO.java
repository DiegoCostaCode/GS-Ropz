package br.fiap.ropz.ropz.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @NotBlank(message = "Email é obrigatório para login")
        @Email(message = "Email inválido")
        String email,
        @NotBlank(message = "Senha é obrigatória para login")
        @Size(min = 3, max = 20, message = "Senha deve ter entre 3 e 20 caracteres")
        String senha
) { }
