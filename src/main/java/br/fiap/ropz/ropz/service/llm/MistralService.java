package br.fiap.ropz.ropz.service.llm;

import br.fiap.ropz.ropz.dto.mistral.MistralPromptResponseDTO;
import br.fiap.ropz.ropz.dto.temperatura.TemperaturaResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MistralService {

    private ChatLanguageModel model;

    @PostConstruct
    public void init() {
        this.model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
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

        return String.format(
            """
                Você é um especialista de clima que trabalha para um programa chamado ROPZ, ou Rede de Observação e Previsão de Zonas Quentes.
                Sua função é analisar os dados meteorológicos atuais e classificar o nível de risco à saúde das pessoas.\s
                
                Pessoas expostas ao calor excessivo podem sofrer com fadiga, desidratação, insolação ou até doenças graves como câncer de pele.
                
                Classifique o risco à saúde com base nos dados abaixo:
                
                ### Dados meteorológicos:
                - Temperatura atual: %.1f°C
                - Temperatura mínima: %.1f°C
                - Temperatura máxima: %.1f°C
                - Sensação térmica: %.1f°C
                - Umidade: %d%%
                
                ### Classificações disponíveis (obrigatório usar uma delas):
                - **BAIXO**: Temperatura confortável e segura.
                - **MODERADO**: Temperatura com leve desconforto, evite exposição prolongada.
                - **ALTO**: Calor elevado, risco de desidratação e insolação.
                - **EXTREMO**: Condições perigosas, pode afetar a saúde gravemente.
                
                Retorne **somente o JSON** com o seguinte formato:
    
                {
                  "relatorioTemperatura": %d,
                  "nivelRisco": "BAIXO" | "MODERADO" | "ALTO" | "EXTREMO",
                  "mensagem": "Recomendação breve aqui."
                }
            """,
                temperaturaResponseDTO.temperatura(),
                temperaturaResponseDTO.temperaturaMinima(),
                temperaturaResponseDTO.temperaturaMaxima(),
                temperaturaResponseDTO.sensacaoTermica(),
                temperaturaResponseDTO.umidade(),
                temperaturaResponseDTO.id()
        );
    }
}

