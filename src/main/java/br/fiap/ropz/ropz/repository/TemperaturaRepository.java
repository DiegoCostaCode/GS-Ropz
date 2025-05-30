package br.fiap.ropz.ropz.repository;

import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.temperatura.EnumOrigem;
import br.fiap.ropz.ropz.model.temperatura.Temperatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemperaturaRepository extends JpaRepository<Temperatura, Long> {

    List<Temperatura> findByLocalizacaoOrderByDataHoraDesc(Localizacao localizacao);

    Optional<Temperatura> findFirstByLocalizacaoAndTipoConsultaOrderByCriadoEmDesc(Localizacao localizacao, EnumOrigem dadosOrigem);
}
