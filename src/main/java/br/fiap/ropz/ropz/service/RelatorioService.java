package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.dto.mistral.MistralPromptResponseDTO;
import br.fiap.ropz.ropz.dto.relatorio.RelatorioResponseDTO;
import br.fiap.ropz.ropz.dto.relatorio.RelatoriosServiceDTO;
import br.fiap.ropz.ropz.dto.temperatura.TemperaturaResponseDTO;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.relatorio.Relatorio;
import br.fiap.ropz.ropz.model.temperatura.EnumOrigem;
import br.fiap.ropz.ropz.model.temperatura.Temperatura;
import br.fiap.ropz.ropz.repository.RelatorioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RelatorioService {

    private static final Logger log = LoggerFactory.getLogger(RelatorioService.class);

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Autowired
    private TemperaturaService temperaturaService;

    public Relatorio saveRelatorioIA(Long temperaturaId, MistralPromptResponseDTO mistralPromptResponseDTO)
    {
        log.info("Iniciando processo de salvar relatório da IA com classificação: [ {} ]", mistralPromptResponseDTO.nivelRisco());

        Temperatura temperatura = temperaturaService.findById(temperaturaId);

        if (temperatura == null) {
            log.error("Temperatura ID não foi encontrada: [ {} ]", temperaturaId);
            throw new IllegalArgumentException("Temperatura não encontrada para o ID fornecido.");
        }

        Relatorio relatorio = new Relatorio();

        relatorio.setClassificacao(mistralPromptResponseDTO.nivelRisco());
        relatorio.setMensagem(mistralPromptResponseDTO.mensagem());
        relatorio.setTemperatura(temperatura);
        relatorio.setCriadoEm(LocalDateTime.now());

        log.info("Salvando relatório temperatura [ {} ] - com classificação [ {} ] e mensagem: [ {} ]", temperatura.getId(), relatorio.getClassificacao().getDescricao(), relatorio.getMensagem());

        try{
            relatorio = relatorioRepository.save(relatorio);
            log.info("Relatório salvo com sucesso! Relatório ID: [ {} ] - Temp ID: [ {} ]", relatorio.getId(), relatorio.getTemperatura().getId());
            return relatorio;
        } catch (Exception e) {
            log.error("Erro ao salvar relatório ID: [ {} ]", e.getMessage());
            throw new RuntimeException("Erro ao salvar relatório.");
        }

    }

    public Relatorio findByRelatorioTemperaturaId(Long temperaturaId) {
        log.info("Buscando relatório temperatura ID: [ {} ]", temperaturaId);
        return relatorioRepository.findByTemperaturaId(temperaturaId).orElse(null);
    }

    public RelatorioResponseDTO relatorioToResponse(Relatorio relatorio) {

        TemperaturaResponseDTO tempResponse = temperaturaService.temperaturaToResponse(relatorio.getTemperatura());

        return new RelatorioResponseDTO(
                relatorio.getId(),
                relatorio.getClassificacao().getDescricao(),
                relatorio.getMensagem(),
                tempResponse,
                relatorio.getTemperatura().getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                relatorio.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
    }

    public List<Relatorio> getRelatorioOrigemCurrent(Localizacao localizacao) {

         return relatorioRepository.findByClassificacaoIsNotNullAndTemperatura_LocalizacaoAndTemperatura_TipoConsultaOrderByCriadoEmDesc(localizacao, EnumOrigem.CURRENT)
                .stream()
                .limit(4)
                .toList();
    }

    public Relatorio getRelatorioOrigemForecast(Localizacao localizacao) {

        return relatorioRepository.findFirstByClassificacaoIsNotNullAndTemperatura_LocalizacaoAndTemperatura_TipoConsulta(localizacao, EnumOrigem.FORECAST).orElse(null);
    }

    public RelatoriosServiceDTO getRelatorios(Localizacao localizacao) {

        log.info("Buscando relatórios para a localização CEP: [ {} ]", localizacao.getCep());

        List<Relatorio> historico = getRelatorioOrigemCurrent(localizacao); //Temperaturas presentes

        Relatorio forecast = getRelatorioOrigemForecast(localizacao); //Temperaturas futuras

        Relatorio maisRecente = historico.stream()
                .max(Comparator.comparing(r -> r.getTemperatura().getDataHora()))
                .orElse(null);

        if(historico.isEmpty() && forecast == null) {
            log.warn("Nenhum relatório encontrado para a localização: {}", localizacao.getCep());

            temperaturaService.consultarTemperaturaAtual(localizacao); //ASYNC
            temperaturaService.consultarMaiorPrevisao(localizacao); //ASYNC
        }

        log.info("Relatórios encontrados: [ {} ] Temperaturas encontradas | Previsão temperatura ID: [ {} ]", historico.size(), forecast != null ? forecast.getTemperatura().getId() : "Nenhum encontrado!");
        return new RelatoriosServiceDTO(historico, forecast, maisRecente);
    }

}
