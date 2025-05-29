package br.fiap.ropz.ropz.repository;

import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.relatorio.EnumRisco;
import br.fiap.ropz.ropz.model.relatorio.Relatorio;
import br.fiap.ropz.ropz.model.temperatura.EnumOrigem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {

    Optional<Relatorio> findByTemperaturaId(Long temperaturaId);

    List<Relatorio> findByClassificacaoIsNotNullAndTemperatura_LocalizacaoAndTemperatura_DadosOrigem(Localizacao localizacao, EnumOrigem dadosOrigem);

    Optional<Relatorio> findFirstByClassificacaoIsNotNullAndTemperatura_LocalizacaoAndTemperatura_DadosOrigem(Localizacao localizacao, EnumOrigem dadosOrigem);

}
