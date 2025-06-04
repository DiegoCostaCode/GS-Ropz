package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.dto.localizacao.LocalizacaoResponseDTO;
import br.fiap.ropz.ropz.dto.temperatura.OpenWeatherForecastResponseDTO;
import br.fiap.ropz.ropz.dto.temperatura.OpenWeatherResponseDTO;
import br.fiap.ropz.ropz.dto.temperatura.TemperaturaRequestDTO;
import br.fiap.ropz.ropz.dto.temperatura.TemperaturaResponseDTO;
import br.fiap.ropz.ropz.messaging.producer.TemperaturaProducer;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.temperatura.EnumOrigem;
import br.fiap.ropz.ropz.model.temperatura.Temperatura;
import br.fiap.ropz.ropz.repository.TemperaturaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Service
public class TemperaturaService {

    private static final Logger log = LoggerFactory.getLogger(TemperaturaService.class);

    private static Integer tempoConsulta = 2;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TemperaturaProducer temperaturaProducer;

    @Value("${openweather.api.key}")
    private String apiKey;

    @Autowired
    private TemperaturaRepository temperaturaRepository;

    @Autowired
    @Lazy
    private LocalizacaoService localizacaoService;

    @Async
    public void consultarTemperaturaAtual(Localizacao localizacao) {
        try {
            log.info("Consultando relatório atual para o CEP: {}", localizacao.getCep());

            Temperatura temperatura = getHistoricoRequesicoesTemperatura(localizacao, EnumOrigem.CURRENT);

            if (isConsultaRecente(temperatura)) {
                log.info("Consulta recente encontrada. Retornando relatório armazenado.");
                return;
            }

            log.info("Fazendo nova consulta ao OpenWeatherMap.");

            String url = montarUrl("current", localizacao.getLatitude(), localizacao.getLongitude());

            OpenWeatherResponseDTO response = restTemplate.getForObject(url, OpenWeatherResponseDTO.class);

            validarRespostaAtual(response, localizacao);

            save(criarDtoAtual(response, localizacao));

            log.info("Dados do clima atual salvos com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao consultar temperatura atual para {}: {}", localizacao.getCep(), e.getMessage(), e);
            throw new RuntimeException("Erro ao consultar temperatura atual.", e);
        }
    }

    @Async
    public void consultarMaiorPrevisao(Localizacao localizacao) {
        try {
            log.info("Consultando maior previsão para o CEP: {}", localizacao.getCep());

            Temperatura previsao = getHistoricoRequesicoesTemperatura(localizacao, EnumOrigem.FORECAST);

            if (previsao != null && previsao.getDataHora().isAfter(LocalDateTime.now())) {
                log.info("Previsão futura válida já existente: {}", previsao.getDataHora());
                return;
            }

            String url = montarUrl("forecast", localizacao.getLatitude(), localizacao.getLongitude());
            OpenWeatherForecastResponseDTO forecast = restTemplate.getForObject(url, OpenWeatherForecastResponseDTO.class);

            validarRespostaForecast(forecast, localizacao);

            var maior = forecast.list().stream()
                    .max(Comparator.comparingDouble(f -> f.main().temp_max()))
                    .orElseThrow(() -> new RuntimeException("Nenhuma previsão encontrada"));

            log.info("Previsão realizada: {}°C em {}", maior.main().temp_max(), maior.dt_txt());

            save(criarDtoForecast(maior, localizacao));
        } catch (Exception e) {
            log.error("Erro ao consultar maior temperatura no futuro para {}: {}", localizacao.getCep(), e.getMessage(), e);
            throw new RuntimeException("Erro ao consultar temperatura futura.", e);
        }
    }

    private String montarUrl(String tipo, double lat, double lon) {
        String endpoint = tipo.equals("current") ? "weather" : "forecast";

        return String.format("https://api.openweathermap.org/data/2.5/%s?lat=%f&lon=%f&units=metric&lang=pt_br&appid=%s", endpoint, lat, lon, apiKey);
    }

    private boolean isConsultaRecente(Temperatura temperatura) {
        if (temperatura == null) return false;
        Duration diferenca = Duration.between(temperatura.getDataHora(), LocalDateTime.now());
        return diferenca.toHours() < tempoConsulta;
    }

    private void validarRespostaAtual(OpenWeatherResponseDTO response, Localizacao localizacao) {
        if (response == null || response.main() == null || response.weather() == null || response.weather().isEmpty()) {
            log.error("Erro ao buscar dados do clima para a localização: {}", localizacao.getCep());
            throw new RuntimeException("Erro ao buscar dados do clima.");
        }
    }

    private void validarRespostaForecast(OpenWeatherForecastResponseDTO forecast, Localizacao localizacao) {
        if (forecast == null || forecast.list() == null || forecast.list().isEmpty()) {
            log.error("Erro ao buscar dados de previsão para a localização: {}", localizacao.getCep());
            throw new RuntimeException("Erro ao buscar dados de previsão.");
        }
    }

    private TemperaturaRequestDTO criarDtoAtual(OpenWeatherResponseDTO response, Localizacao localizacao) {

        var weather = response.weather().get(0);

        return new TemperaturaRequestDTO(
                "https://openweathermap.org/img/wn/" + weather.icon() + "@2x.png",
                weather.main(),
                weather.description(),
                response.main().temp(),
                response.main().temp_max(),
                response.main().temp_min(),
                response.main().feels_like(),
                response.main().humidity(),
                LocalDateTime.now(),
                LocalDateTime.ofInstant(Instant.ofEpochSecond(response.dt()), ZoneId.of("America/Sao_Paulo")),
                localizacao,
                EnumOrigem.CURRENT
        );
    }

    private TemperaturaRequestDTO criarDtoForecast(OpenWeatherForecastResponseDTO.ForecastEntry maior, Localizacao localizacao) {

        var weather = maior.weather().get(0);

        return new TemperaturaRequestDTO(
                "https://openweathermap.org/img/wn/" + weather.icon() + "@2x.png",
                weather.main(),
                weather.description(),
                maior.main().temp(),
                maior.main().temp_max(),
                maior.main().temp_min(),
                maior.main().feels_like(),
                maior.main().humidity(),
                LocalDateTime.now(),
                LocalDateTime.parse(maior.dt_txt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                localizacao,
                EnumOrigem.FORECAST
        );
    }

    public Temperatura save(TemperaturaRequestDTO temperaturaRequest) {

        log.info("Salvando a temperatura: [ {} °C]", temperaturaRequest.valorCelsius());

        Temperatura temperatura = new Temperatura();

        temperatura.setIcon(temperaturaRequest.icon());
        temperatura.setTempo(temperaturaRequest.tempo());
        temperatura.setDescricao(temperaturaRequest.descricao());
        temperatura.setTemperatura(temperaturaRequest.valorCelsius());
        temperatura.setTemperaturaMaxima(temperaturaRequest.maxValorCelsius());
        temperatura.setTemperaturaMinima(temperaturaRequest.minValorCelsius());
        temperatura.setUmidade(temperaturaRequest.umidade());
        temperatura.setSensacaoTermica(temperaturaRequest.sensacaoTermica());
        temperatura.setCriadoEm(temperaturaRequest.criadoEm());
        temperatura.setDataHora(temperaturaRequest.dataHora());
        temperatura.setTipoConsulta(temperaturaRequest.tipoConsulta());
        temperatura.setLocalizacao(temperaturaRequest.localizacao());

        try{
            Temperatura temperaturaSalva = temperaturaRepository.save(temperatura);

            log.info("Temperatura salva com sucesso! [ {} ] - Temperatura [ {} °C]", temperaturaSalva.getId(), temperaturaSalva.getTemperatura());

            temperaturaProducer.enviarParaAnalise(temperaturaToResponse(temperaturaSalva));
            return temperaturaSalva;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Temperatura findById(Long id) {
        log.info("Buscando temperatura [ {} ]", id);

        return temperaturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Temperatura não encontrada para o ID: " + id));
    }

    public Temperatura getHistoricoRequesicoesTemperatura(Localizacao localizacao, EnumOrigem dadosOrigem) {
        log.info("Consultando histórico de requisições de temperatura para a localização: [ {} ] e de tipo: [ {} ]", localizacao.getCep(), dadosOrigem);
        return temperaturaRepository.findFirstByLocalizacaoAndTipoConsultaOrderByCriadoEmDesc(localizacao, dadosOrigem).orElse(null);
    }

    public TemperaturaResponseDTO temperaturaToResponse(Temperatura temperatura) {

        LocalizacaoResponseDTO localResponse = localizacaoService.localizacaoResponseDTO(temperatura.getLocalizacao());

        return new TemperaturaResponseDTO(
                temperatura.getId(),
                temperatura.getIcon(),
                temperatura.getTempo(),
                temperatura.getDescricao(),
                temperatura.getTemperatura(),
                temperatura.getTemperaturaMinima(),
                temperatura.getTemperaturaMaxima(),
                temperatura.getSensacaoTermica(),
                temperatura.getUmidade(),
                localResponse,
                temperatura.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }
}

