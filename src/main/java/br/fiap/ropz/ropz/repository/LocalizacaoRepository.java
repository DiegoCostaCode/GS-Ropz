package br.fiap.ropz.ropz.repository;

import br.fiap.ropz.ropz.model.Localizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {

    Optional<Localizacao> findByCep(String cep);
}
