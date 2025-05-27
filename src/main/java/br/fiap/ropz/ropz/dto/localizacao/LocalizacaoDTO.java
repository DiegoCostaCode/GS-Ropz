package br.fiap.ropz.ropz.dto.localizacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalizacaoDTO {

    @NotBlank(message = "CEP deve ser preenchido")
    @Pattern(regexp = "^[0-9]{5}-[0-9]{3}$", message = "O CEP deve estar no formato 12345-678")
    @Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
    private String cep;

    @NotBlank(message = "Bairro deve ser preenchido")
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    private String bairro;

    @NotBlank(message = "Cidade deve ser preenchida")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @NotBlank(message = "Estado deve ser preenchido")
    @Size(max = 100, message = "Estado deve ter no máximo 100 caracteres")
    private String estado;

    @NotBlank(message = "Latitude deve ser preenchida")
    @Pattern(regexp = "^-?\\d{1,2}\\.\\d{1,6}$", message = "Latitude deve estar no formato -90.000000 a 90.000000")
    @Size(min= 1, max = 10, message = "Latitude deve ter no máximo 10 caracteres")
    private String latitude;

    @NotBlank(message = "Longitude deve ser preenchida")
    @Pattern(regexp = "^-?\\d{1,3}\\.\\d{1,6}$", message = "Longitude deve estar no formato -180.000000 a 180.000000")
    @Size(min= 1, max = 11, message = "Longitude deve ter no máximo 11 caracteres")
    private String longitude;
}
