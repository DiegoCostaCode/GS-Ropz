package br.fiap.ropz.ropz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ropz_localizacao")
public class Localizacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cep", length = 9)
    private String cep;

    @Column(name = "bairro", length = 100)
    private String bairro;

    @Column(name = "bairro", length = 100)
    private String cidade;

    @Column(name = "bairro", length = 100)
    private String estado;
}
