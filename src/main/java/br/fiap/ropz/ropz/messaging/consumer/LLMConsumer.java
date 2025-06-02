package br.fiap.ropz.ropz.messaging.consumer;

import br.fiap.ropz.ropz.dto.mistral.MistralPromptResponseDTO;
import br.fiap.ropz.ropz.dto.temperatura.TemperaturaResponseDTO;
import br.fiap.ropz.ropz.service.RelatorioService;
import br.fiap.ropz.ropz.service.UsuarioService;
import br.fiap.ropz.ropz.service.llm.MistralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LLMConsumer {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private MistralService mistralService;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void receber(TemperaturaResponseDTO temperaturaResponseDTO) {

        log.info("Recebendo relatorio para análise {} - CEP {}",
                temperaturaResponseDTO.id(),
                temperaturaResponseDTO.localizacao().cep()
        );

        MistralPromptResponseDTO mistralResponse = mistralService.analisarRelatorio(temperaturaResponseDTO);

        if (mistralResponse == null) {
            log.error("Erro ao analisar relatorio com Mistral.");
            throw new RuntimeException("Erro ao analisar relatorio.");
        }

        log.info("Relatorio analisado com sucesso: {}", mistralResponse);

        relatorioService.saveRelatorioIA(mistralResponse);
    }
}
