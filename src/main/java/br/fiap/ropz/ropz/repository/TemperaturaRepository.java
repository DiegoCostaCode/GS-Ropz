package br.fiap.ropz.ropz.repository;

import br.fiap.ropz.ropz.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemperaturaRepository extends JpaRepository<Usuario, Long> {
}
