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

    @NotBlank(message = "Nome não pode ser vazio")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;
    @NotBlank(message = "Telefone deve ser preenchido")
    @Pattern(regexp = "^\\(?[1-9]{2}\\)? ?(?:[2-8]|9[1-9])[0-9]{3}\\-?[0-9]{4}$",
            message = "O telefone deve estar no formato (DDD) 91234-5678 ou (DDD) 1234-5678")
    private String telefone;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Não foi definido uma senha para a clínica")
    @Size(min = 3, max = 20, message = "Senha deve ter entre 3 e 20 caracteres")
    private String senha;

    @NotBlank(message = "CEP deve ser preenchido")
    @Pattern(regexp = "^[0-9]{5}-[0-9]{3}$", message = "O CEP deve estar no formato 12345-678")
    @Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
    private String cep;

}
