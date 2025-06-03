package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.dto.localizacao.LocalizacaoDTO;
import br.fiap.ropz.ropz.dto.localizacao.LocalizacaoResponseDTO;
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

    private static final Logger log = LoggerFactory.getLogger(LocalizacaoService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    public Localizacao buscarPorCep(String cep) {

        log.info("Buscando localização por CEP: [ {} ]", cep);
        Localizacao localizacao = localizacaoRepository.findByCep(cep).orElse(null);

        if (localizacao != null) {
            log.info("Localização [ {} ] encontrada no banco de dados.", cep);
            return localizacao;
        }

        String urlViaCep = montarUrl(cep);
        ViaCepResponse viaCep = restTemplate.getForObject(urlViaCep, ViaCepResponse.class);

        log.info("ViaCep: {}", viaCep);

        if (viaCep == null || viaCep.cep() == null) {
            log.error("Erro ao buscar CEP: [ {} ]", cep);
            throw new IllegalArgumentException("CEP inválido ou não encontrado: " + cep);
        }

        String url = montarUrlNomiatim(cep, viaCep.localidade(), viaCep.uf());

        log.info("Buscando coordenadas para o CEP: [ {} ] com a URL: [ {} ]", cep, url);

        NominatimResponse[] coordenadasResponse = restTemplate.getForObject(url, NominatimResponse[].class);

        if (coordenadasResponse == null || coordenadasResponse.length == 0) {
            log.error("Erro ao buscar coordenadas para o CEP: [ {} ]", cep);
            throw new IllegalArgumentException("Coordenadas não encontradas para o CEP: " + cep);
        }

        NominatimResponse coordenadas = coordenadasResponse[0];

        log.info("Coordenadas do CEP [ {} ] encontradas: [ {} ]", cep, coordenadas);

        if (coordenadas == null || coordenadas.lat() == null || coordenadas.lon() == null) {
            log.error("Erro ao buscar coordenadas para o CEP: [ {} ]", cep);
            throw new IllegalArgumentException("Coordenadas não encontradas para o CEP: " + cep);
        }

        LocalizacaoDTO localizacaoDTO = new LocalizacaoDTO();

        localizacaoDTO.setCep(viaCep.cep());
        localizacaoDTO.setBairro(viaCep.bairro());
        localizacaoDTO.setCidade(viaCep.localidade());
        localizacaoDTO.setEstado(viaCep.uf());
        localizacaoDTO.setLatitude(Double.parseDouble(coordenadas.lat()));
        localizacaoDTO.setLongitude(Double.parseDouble(coordenadas.lon()));

        return save(localizacaoDTO);
    }

    public String montarUrlNomiatim(String cep, String cidade, String estado) {
        return "https://nominatim.openstreetmap.org/search?format=json&q=" + cep + "," + cidade + "," + estado;
    }

    public String montarUrl(String cep){
        return "https://viacep.com.br/ws/" + cep + "/json/";
    }

    public Localizacao save(LocalizacaoDTO localizacaoDTO) {

        log.info("Salvando localização CEP: [ {} ]", localizacaoDTO.getCep());

        Localizacao localizacao = new Localizacao();

        localizacao.setCep(localizacaoDTO.getCep());
        localizacao.setBairro(localizacaoDTO.getBairro());
        localizacao.setCidade(localizacaoDTO.getCidade());
        localizacao.setEstado(localizacaoDTO.getEstado());
        localizacao.setLatitude(localizacaoDTO.getLatitude());
        localizacao.setLongitude(localizacaoDTO.getLongitude());


        try{
            Localizacao newLocalizacao = localizacaoRepository.save(localizacao);
            log.info("Localização salva com sucesso! ID [ {} ] - Cep [{}]", newLocalizacao.getId(), newLocalizacao.getCep());
            return newLocalizacao;
        }
        catch (Exception e) {
            log.error("Erro ao criar localização CEP: [{}]", localizacaoDTO.getCep(), e);
            throw new RuntimeException("Erro ao criar localização");
        }
    }

    public LocalizacaoResponseDTO localizacaoResponseDTO(Localizacao localizacao) {

        return new LocalizacaoResponseDTO(
            localizacao.getId(),
            localizacao.getCep(),
            localizacao.getBairro(),
            localizacao.getCidade(),
            localizacao.getEstado(),
            localizacao.getLatitude(),
            localizacao.getLongitude()
        );
    }

    public Localizacao findById(Long id) {
        log.info("Consultando localização por ID: [ {} ]", id);
        return localizacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nenhuma localização encontrada com o ID: " + id));
    }
}
