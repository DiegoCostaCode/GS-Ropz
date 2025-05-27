package br.fiap.ropz.ropz.dto.localizacao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalizacaoDTO {

    private String cep;

    private String bairro;

    private String cidade;

     private String estado;

    private Double latitude;

    private Double longitude;
}
