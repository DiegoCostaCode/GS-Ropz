package br.fiap.ropz.ropz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ropz_usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, length = 100)
    private String nome;
    @Column(name = "data_cadastro")
    private Date dataCadastro;
    @Column(name = "telefone", length = 17, unique = true)
    private String telefone;
    @OneToOne
    @JoinColumn(name = "localizacao_id", referencedColumnName = "id")
    private Localizacao localizacao;
}
