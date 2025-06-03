package br.fiap.ropz.ropz.service.llm;

import br.fiap.ropz.ropz.dto.mistral.MistralPromptResponseDTO;
import br.fiap.ropz.ropz.dto.temperatura.TemperaturaResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class MistralService {

    private ChatLanguageModel model;

    @Value("${mistral.ip.server}")
    private String serverIP;

    @PostConstruct
    public void init() {
        this.model = OllamaChatModel.builder()
                .baseUrl("http://" + serverIP + ":11434")
                .modelName("mistral")
                .build();
    }

    public MistralPromptResponseDTO analisarRelatorio(TemperaturaResponseDTO temperaturaResponseDTO) {

        String analise = model.generate(gerarPrompt(temperaturaResponseDTO));

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(analise, MistralPromptResponseDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar a resposta da IA", e);
        }
    }

    public String gerarPrompt(TemperaturaResponseDTO temperaturaResponseDTO) {
        log.info("Gerando prompt para análise de temperatura ID: [ {} }", temperaturaResponseDTO.id());

        return String.format(
                """
                Você é um especialista em clima da ROPZ, dando alertas práticos sobre temperaturas extremas.
        
                Sua tarefa:
                1. Analisar os dados meteorológicos
                2. Escolher um nível de risco: BAIXO, MODERADO, ALTO ou EXTREMO
                3. Criar uma mensagem curta com orientações úteis
        
                ### Dados:
                - Temperatura atual: %.1f°C
                - Sensação térmica: %.1f°C
                - Mínima/Máxima: %.1f°C/%.1f°C
                - Umidade: %d%%
        
                ### Regras para a mensagem:
                - Linguagem simples e direta (como conversa informal)
                - Em calor: sugerir roupas leves, quantidade de água e proteção solar
                - Em frio extremo: orientar sobre agasalhos
                - Máximo 2 frases
                - Exemplos válidos:
                  "Está muito quente! Use roupas leves de algodão, tome pelo menos 3L de água e evite sol entre 10h-16h."
                  "Temperatura agradável. Hidrate-se normalmente e use roupas confortáveis."
                  "Frio intenso! Use várias camadas de roupa, luvas e evite exposição prolongada."
        
                ### Formato obrigatório (somente JSON):
                {
                  "nivelRisco": "BAIXO" | "MODERADO" | "ALTO" | "EXTREMO",
                  "mensagem": "Orientações curtas aqui"
                }
                """,
                temperaturaResponseDTO.temperatura(),
                temperaturaResponseDTO.sensacaoTermica(),
                temperaturaResponseDTO.temperaturaMinima(),
                temperaturaResponseDTO.temperaturaMaxima(),
                temperaturaResponseDTO.umidade()
        );
    }
}

