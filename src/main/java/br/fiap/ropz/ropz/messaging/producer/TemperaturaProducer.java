package br.fiap.ropz.ropz.messaging.producer;

import br.fiap.ropz.ropz.dto.temperatura.TemperaturaResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TemperaturaProducer {

    private static final Logger log = LoggerFactory.getLogger(TemperaturaProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;


    public TemperaturaProducer(RabbitTemplate rabbitTemplate,
                               @Value("${app.rabbitmq.exchange}") String exchange,
                               @Value("${app.rabbitmq.routingkey}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void enviarParaAnalise(TemperaturaResponseDTO temperaturaResponseDTO) {
        log.info("Enviando a analise de relatorio...{}", temperaturaResponseDTO.id());
        rabbitTemplate.convertAndSend(exchange, routingKey, temperaturaResponseDTO);
        log.info("Mensagem enviada para a fila com sucesso.");
    }
}
