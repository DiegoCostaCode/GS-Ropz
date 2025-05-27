package br.fiap.ropz.ropz.repository;

import br.fiap.ropz.ropz.model.Temperatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemperaturaRepository extends JpaRepository<Temperatura, Long> {

    List<Temperatura> findByLocalizacaoOrderByDataHoraDesc(Long localizacaoId);
}
