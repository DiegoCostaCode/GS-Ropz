package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.dto.mistral.MistralPromptResponseDTO;
import br.fiap.ropz.ropz.dto.relatorio.RelatorioResponseDTO;
import br.fiap.ropz.ropz.dto.relatorio.RelatoriosServiceDTO;
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
import java.util.List;

@Service
public class RelatorioService {

    private static final Logger log = LoggerFactory.getLogger(RelatorioService.class);

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Autowired
    private TemperaturaService temperaturaService;

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

        return new RelatorioResponseDTO(
                relatorio.getId(),
                relatorio.getClassificacao().getDescricao(),
                relatorio.getMensagem(),
                relatorio.getTemperatura().getId(),
                relatorio.getTemperatura().getIcon(),
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

        int tentativas = 0;
        int esperar = 2000;
        int limiteTentativas = 10;

        log.info("Buscando dados da tela de relatórios");

        while (tentativas < limiteTentativas) {

            log.info("Tentativa {} de {} para buscar relatórios de temperatura.", tentativas + 1, limiteTentativas);

            List<Relatorio> listaRelatoriosCurrent = getRelatorioOrigemCurrent(localizacao);
            Relatorio relatorioForecast = getRelatorioOrigemForecast(localizacao);

            boolean temAtual = listaRelatoriosCurrent != null && !listaRelatoriosCurrent.isEmpty();
            boolean temForecast = relatorioForecast != null;

            if (temAtual && temForecast) {
                log.info("Relatórios de temperatura encontrados!");
                Relatorio relatorioMaisRecente = listaRelatoriosCurrent.get(0);
                return new RelatoriosServiceDTO(listaRelatoriosCurrent, relatorioForecast, relatorioMaisRecente );
            }

            try {
                log.warn("Aguardando {} milissegundos antes da próxima tentativa...", esperar);
                Thread.sleep(esperar);
            } catch (InterruptedException e) {
                log.error("Erro ao aguardar entre as tentativas: {}", e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }

            tentativas++;
        }

        log.warn("Não foi possível obter todos os relatórios após {} tentativas.", limiteTentativas);
        return null;
    }
}
