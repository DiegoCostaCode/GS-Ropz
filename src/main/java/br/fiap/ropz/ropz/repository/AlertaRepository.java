package br.fiap.ropz.ropz.repository;

import br.fiap.ropz.ropz.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
}
