package br.fiap.ropz.ropz.repository;

import br.fiap.ropz.ropz.model.Credenciais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredenciaisRepository extends JpaRepository<Credenciais, Long> {
}
