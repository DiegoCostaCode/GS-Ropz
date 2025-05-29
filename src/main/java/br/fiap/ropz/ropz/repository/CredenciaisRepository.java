package br.fiap.ropz.ropz.repository;

import br.fiap.ropz.ropz.model.Usuario;
import br.fiap.ropz.ropz.model.usuario.Credenciais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredenciaisRepository extends JpaRepository<Credenciais, Long> {

    Optional<Credenciais> findCredenciaisByUsuario(Usuario usuario);
}
