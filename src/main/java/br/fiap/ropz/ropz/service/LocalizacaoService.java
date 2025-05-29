package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.dto.localizacao.LocalizacaoDTO;
import br.fiap.ropz.ropz.dto.localizacao.NominatimResponse;
import br.fiap.ropz.ropz.dto.localizacao.ViaCepResponse;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.repository.LocalizacaoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class LocalizacaoService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    public Localizacao buscarPorCep(String cep) {

        log.info("Buscando localização por CEP: {}", cep);
        Localizacao localizacao = localizacaoRepository.findByCep(cep).orElse(null);

        if (localizacao != null) {
            log.info("Localização encontrada no banco de dados: {}", localizacao);
            return localizacao;
        }

        log.info("Localização não encontrada no banco de dados, buscando via API");

        String urlViaCep = "https://viacep.com.br/ws/" + cep + "/json/";
        ViaCepResponse viaCep = restTemplate.getForObject(urlViaCep, ViaCepResponse.class);

        log.info("ViaCep: {}", viaCep);

        if (viaCep == null || viaCep.cep() == null) {
            log.error("Erro ao buscar CEP: {}", cep);
            throw new IllegalArgumentException("CEP inválido ou não encontrado: " + cep);
        }

        String query = String.format("%s,%s,%s", viaCep.cep(), viaCep.localidade(), viaCep.uf());
        String urlNominatim = "https://nominatim.openstreetmap.org/search?format=json&q=" + query;

        NominatimResponse[] coordenadasResponse = restTemplate.getForObject(urlNominatim, NominatimResponse[].class);

        if (coordenadasResponse == null || coordenadasResponse.length == 0) {
            log.error("Erro ao buscar coordenadas para o CEP: {}", cep);
            throw new IllegalArgumentException("Coordenadas não encontradas para o CEP: " + cep);
        }

        NominatimResponse coordenadas = coordenadasResponse[0];

        log.info("Coordenadas encontradas: {}", coordenadas);

        if (coordenadas == null || coordenadas.lat() == null || coordenadas.lon() == null) {
            log.error("Erro ao buscar coordenadas para o CEP: {}", cep);
            throw new IllegalArgumentException("Coordenadas não encontradas para o CEP: " + cep);
        }

        LocalizacaoDTO localizacaoDTO = new LocalizacaoDTO();

        localizacaoDTO.setCep(viaCep.cep());
        localizacaoDTO.setBairro(viaCep.bairro());
        localizacaoDTO.setCidade(viaCep.localidade());
        localizacaoDTO.setEstado(viaCep.uf());
        localizacaoDTO.setLatitude(Double.parseDouble(coordenadas.lat()));
        localizacaoDTO.setLongitude(Double.parseDouble(coordenadas.lon()));

        log.info("Localização encontrada via API");

        return save(localizacaoDTO);
    }

    public Localizacao save(LocalizacaoDTO localizacaoDTO) {

        log.info("Salvando localização");

        Localizacao localizacao = new Localizacao();

        localizacao.setCep(localizacaoDTO.getCep());
        localizacao.setBairro(localizacaoDTO.getBairro());
        localizacao.setCidade(localizacaoDTO.getCidade());
        localizacao.setEstado(localizacaoDTO.getEstado());
        localizacao.setLatitude(localizacaoDTO.getLatitude());
        localizacao.setLongitude(localizacaoDTO.getLongitude());

        return localizacaoRepository.save(localizacao);
    }

    public Localizacao findById(Long id) {
        log.info("Consultando localização por ID: {}", id);
        return localizacaoRepository.findById(id).orElse(null);
    }
}
