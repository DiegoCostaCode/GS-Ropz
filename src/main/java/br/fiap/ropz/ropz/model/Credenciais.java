package br.fiap.ropz.ropz.model;

import br.fiap.ropz.ropz.model.usuario.Enum_tipo_usuario;
import br.fiap.ropz.ropz.model.usuario.Usuario;
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
@Table(name = "ropz_credenciais")
public class Credenciais {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name="email", length = 100)
    private String email;

    @Column(name="senha")
    private String senha;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_usuario")
    private Enum_tipo_usuario tipo;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;
}
