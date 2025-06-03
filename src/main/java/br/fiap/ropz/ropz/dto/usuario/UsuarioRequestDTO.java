package br.fiap.ropz.ropz.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "{validation.blank.nome}")
    @Size(min= 3, max = 50, message = "{validation.size.nome}")
    private String nome;

    @NotBlank(message = "{validation.blank.telefone}")
    @Pattern(regexp = "^\\(?[1-9]{2}\\)? ?(?:[2-8]|9[1-9])[0-9]{3}\\-?[0-9]{4}$",
            message = "{validation.pattern.telefone}")
    private String telefone;

    @NotBlank(message = "{validation.blank.email}")
    @Email(message = "{validation.email.email}")
    private String email;

    @NotBlank(message = "{validation.blank.senha}")
    @Size(min = 3, max = 20, message = "{validation.size.senha}")
    private String senha;

    @NotBlank(message = "{validation.blank.cep}")
    @Pattern(regexp = "^[0-9]{5}-[0-9]{3}$", message = "{validation.pattern.cep}")
    @Size(max = 9, message = "{validation.size.cep}")
    private String cep;

}
