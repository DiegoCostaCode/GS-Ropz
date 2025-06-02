package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.dto.localizacao.LocalizacaoResponseDTO;
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
    @Autowired
    private LocalizacaoService localizacaoService;

    public Relatorio saveRelatorioIA(MistralPromptResponseDTO mistralPromptResponseDTO)
    {
        log.info("Iniciando processo de salvar relatório da IA com classificação: {}", mistralPromptResponseDTO.nivelRisco());

        Temperatura temperatura = temperaturaService.findById(mistralPromptResponseDTO.relatorioTemperatura());

        if (temperatura == null) {
            log.error("Temperatura ID não foi encotrada: {}", mistralPromptResponseDTO.relatorioTemperatura());
            throw new IllegalArgumentException("Temperatura não encontrada para o ID fornecido.");
        }

        Relatorio relatorio = new Relatorio();
        relatorio.setClassificacao(mistralPromptResponseDTO.nivelRisco());
        relatorio.setMensagem(mistralPromptResponseDTO.mensagem());
        relatorio.setTemperatura(temperatura);
        relatorio.setCriadoEm(LocalDateTime.now());

        log.info("Salvando relatório com classificação: {} e mensagem: {}", relatorio.getClassificacao().getDescricao(), relatorio.getMensagem());

        return relatorioRepository.save(relatorio);
    }

    public Relatorio findByRelatorioTemperaturaId(Long temperaturaId) {
        log.info("Buscando relatório pelo ID da temperatura: {}", temperaturaId);
        return relatorioRepository.findByTemperaturaId(temperaturaId).orElse(null);
    }

    public RelatorioResponseDTO relatorioToResponse(Relatorio relatorio) {

        TemperaturaResponseDTO tempResponse = temperaturaService.temperaturaToResponse(relatorio.getTemperatura());

        return new RelatorioResponseDTO(
                relatorio.getId(),
                relatorio.getClassificacao().getDescricao(),
                relatorio.getMensagem(),
                tempResponse,
                relatorio.getTemperatura().getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                relatorio.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
    }

    public List<Relatorio> getRelatorioOrigemCurrent(Localizacao localizacao) {
        log.info("Trazendo os 4 últimos registros de relatorio para a localização: {}", localizacao.getCep());

         return relatorioRepository.findByClassificacaoIsNotNullAndTemperatura_LocalizacaoAndTemperatura_TipoConsultaOrderByCriadoEmDesc(localizacao, EnumOrigem.CURRENT)
                .stream()
                .limit(4)
                .toList();
    }

    public Relatorio getRelatorioOrigemForecast(Localizacao localizacao) {
        log.info("Trazendo relatorio de previsão: {}", localizacao.getCep());

        return relatorioRepository.findFirstByClassificacaoIsNotNullAndTemperatura_LocalizacaoAndTemperatura_TipoConsulta(localizacao, EnumOrigem.FORECAST).orElse(null);
    }

    public RelatoriosServiceDTO getRelatorios(Localizacao localizacao) {

        temperaturaService.consultarTemperaturaAtual(localizacao);
        temperaturaService.consultarMaiorPrevisao(localizacao);

        List<Relatorio> historico = getRelatorioOrigemCurrent(localizacao);

        Relatorio forecast = getRelatorioOrigemForecast(localizacao);

        Relatorio maisRecente = historico.stream()
                .max(Comparator.comparing(r -> r.getTemperatura().getDataHora()))
                .orElse(null);

        return new RelatoriosServiceDTO(historico, forecast, maisRecente);
    }

}
