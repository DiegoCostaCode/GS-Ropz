package br.fiap.ropz.ropz.model.temperatura;

import br.fiap.ropz.ropz.model.Localizacao;
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
@Table(name = "ropz_temperatura")
public class Temperatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "openweather_icon")
    private  String icon;

    @Column(name = "tempo", length = 10)
    private String tempo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "temperatura", precision = 5)
    private Double temperatura;

    @Column(name = "temperatura_max", precision = 5)
    private Double temperaturaMaxima;

    @Column(name = "temperatura_min", precision = 5)
    private Double temperaturaMinima;

    @Column(name = "sensacao_termica", precision = 5)
    private Double sensacaoTermica;

    @Column(name = "umidade", precision = 5)
    private Integer umidade;

    @Column(name= "criado_em")
    private LocalDateTime criadoEm;

    @Column(name= "data_hora")
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "dados_origem")
    private EnumOrigem dadosOrigem;

    @ManyToOne
    @JoinColumn(name = "localizacao_id", referencedColumnName = "id")
    private Localizacao localizacao;
}

