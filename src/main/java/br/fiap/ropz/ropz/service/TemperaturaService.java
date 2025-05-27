package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.dto.temperatura.OpenWeatherResponseDTO;
import br.fiap.ropz.ropz.dto.temperatura.TemperaturaRequestDTO;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.Temperatura;
import br.fiap.ropz.ropz.repository.TemperaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TemperaturaService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${openweather.api.key}")
    private String apiKey;

    @Autowired
    private TemperaturaRepository temperaturaRepository;

    public Temperatura consultarTemperaturaAtual(Localizacao localizacao) {

        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric&lang=pt_br&appid=%s",
                localizacao.getLatitude(),
                localizacao.getLongitude(),
                apiKey
        );

        OpenWeatherResponseDTO response = restTemplate.getForObject(url, OpenWeatherResponseDTO.class);

        if (response == null || response.main() == null || response.weather().isEmpty()) {
            throw new RuntimeException("Erro ao buscar dados do clima.");
        }

        System.out.println(response.main().humidity());

        TemperaturaRequestDTO temperaturaRequest = new TemperaturaRequestDTO(
                response.weather().get(0).icon(),
                response.weather().get(0).main(),
                response.weather().get(0).description(),
                response.main().temp(),
                response.main().temp_max(),
                response.main().temp_min(),
                response.main().feels_like(),
                response.main().humidity(),
                LocalDateTime.now(),
                localizacao
        );

        return save(temperaturaRequest);
    }

    public Temperatura save(TemperaturaRequestDTO temperaturaRequest) {
        Temperatura temperatura = new Temperatura();

        System.out.println(temperaturaRequest.umidade());

        temperatura.setIcon(temperaturaRequest.icon());
        temperatura.setTempo(temperaturaRequest.tempo());
        temperatura.setDescricao(temperaturaRequest.descricao());
        temperatura.setTemperatura(temperaturaRequest.valorCelsius());
        temperatura.setTemperaturaMaxima(temperaturaRequest.maxValorCelsius());
        temperatura.setTemperaturaMinima(temperaturaRequest.minValorCelsius());
        temperatura.setUmidade(temperaturaRequest.umidade());
        temperatura.setSensacaoTermica(temperaturaRequest.sensacaoTermica());
        temperatura.setDataHora(temperaturaRequest.dataHora());
        temperatura.setLocalizacao(temperaturaRequest.localizacao());

        return temperaturaRepository.save(temperatura);
    }

    public List<Temperatura> getHistoricoTemperaturas(Long localizacao) {
        return temperaturaRepository.findByLocalizacaoOrderByDataHoraDesc(localizacao)
                .stream()
                .limit(4)
                .toList();
    }
}

