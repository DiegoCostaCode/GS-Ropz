package br.fiap.ropz.ropz.repository;

import br.fiap.ropz.ropz.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCredenciaisEmail(String email);
}
