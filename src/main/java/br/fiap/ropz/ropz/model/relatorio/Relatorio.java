package br.fiap.ropz.ropz.model.relatorio;

import br.fiap.ropz.ropz.model.temperatura.Temperatura;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ropz_relatorio")
public class Relatorio {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "classificacao")
    private EnumRisco classificacao;

    @Column(name = "mensagem")
    private String mensagem;

    @ManyToOne
    @JoinColumn(name = "temp_id", referencedColumnName = "id")
    private Temperatura temperatura;

    @Column(name= "criado_em")
    private LocalDateTime criadoEm;

}
