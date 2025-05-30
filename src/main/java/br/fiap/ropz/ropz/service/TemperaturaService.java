package br.fiap.ropz.ropz.service;

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

    public void consultarTemperaturaAtual(Localizacao localizacao) {

        log.info("Consultando relatorio atual para a localização: {}", localizacao.getCep());

        Temperatura temperatura = getHistoricoRequesicoesTemperatura(localizacao, EnumOrigem.CURRENT);

        if (temperatura != null) {
            Duration diferenca = Duration.between(temperatura.getDataHora(), LocalDateTime.now());

            if (diferenca.toHours() < tempoConsulta) {
                log.info("Consulta recente encontrada, retornando relatorio armazenada.");
                return;
            } else {
                log.info("A última consulta foi realizada a {} horas, buscando dados atualizados.", diferenca.toMinutes());
            }
        }

        log.info("Nenhuma consulta recente encontrada, buscando dados do OpenWeatherMap.");

        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric&lang=pt_br&appid=%s",
                localizacao.getLatitude(),
                localizacao.getLongitude(),
                apiKey
        );

        OpenWeatherResponseDTO response = restTemplate.getForObject(url, OpenWeatherResponseDTO.class);

        validarRespostaAtual(response, localizacao);

        log.info("Dados do clima obtidos com sucesso");

        TemperaturaRequestDTO temperaturaRequestDTO = criarDtoAtual(response, localizacao);

        save(temperaturaRequestDTO);
    }

    public void consultarMaiorPrevisao(Localizacao localizacao) {

        log.info("Consultando previsão futura para maior temperatura em: {}", localizacao.getCep());

        Temperatura ultimaPrevisao = getHistoricoRequesicoesTemperatura(localizacao, EnumOrigem.FORECAST);

        if (ultimaPrevisao != null && ultimaPrevisao.getDataHora().isAfter(LocalDateTime.now())) {
            log.info("Já existe uma previsão futura válida até: {}", ultimaPrevisao.getDataHora());
            return;
        }

        log.info("Fazendo nova requisição de forecast...");

        String url = String.format(
                "https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=metric&lang=pt_br&appid=%s",
                localizacao.getLatitude(),
                localizacao.getLongitude(),
                apiKey
        );

        OpenWeatherForecastResponseDTO forecast = restTemplate.getForObject(url, OpenWeatherForecastResponseDTO.class);

        validarRespostaForecast(forecast, localizacao);

        var maior = forecast.list().stream()
                .max(Comparator.comparingDouble(f -> f.main().temp_max()))
                .orElseThrow(() -> new RuntimeException("Nenhuma previsão encontrada"));

        log.info("Maior previsão encontrada: {}°C em {}", maior.main().temp_max(), maior.dt_txt());

        TemperaturaRequestDTO temperaturaRequestDTO = criarDtoForecast(maior, localizacao);

        save(temperaturaRequestDTO);
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
        log.info("Salvando a relatorio atual");

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

        Temperatura temperaturaSalva = temperaturaRepository.save(temperatura);

        temperaturaProducer.enviarParaAnalise(temperaturaMensagemDTO(temperaturaSalva));

        return temperaturaSalva;
    }

    public TemperaturaResponseDTO temperaturaMensagemDTO(Temperatura temperatura) {
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
                temperatura.getLocalizacao().getId(),
                temperatura.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
    }

    public Temperatura findById(Long id) {
        log.info("Buscando relatorio por ID: {}", id);
        return temperaturaRepository.findById(id).orElse(null);
    }

    public Temperatura getHistoricoRequesicoesTemperatura(Localizacao localizacao, EnumOrigem dadosOrigem) {
        log.info("Consultando a última se há uma consulta recente na mesma localização");
        return temperaturaRepository.findFirstByLocalizacaoAndTipoConsultaOrderByCriadoEmDesc(localizacao, dadosOrigem).orElse(null);
    }

    public TemperaturaResponseDTO temperaturaToResponse(Temperatura temperatura) {
        log.info("Convertendo relatorio para DTO de resposta");

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
                temperatura.getLocalizacao().getId(),
                temperatura.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
    }
}

