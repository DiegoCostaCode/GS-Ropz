package br.fiap.ropz.ropz.controller;

import br.fiap.ropz.ropz.dto.relatorio.RelatorioResponseDTO;
import br.fiap.ropz.ropz.dto.relatorio.RelatoriosServiceDTO;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.relatorio.Relatorio;
import br.fiap.ropz.ropz.service.LocalizacaoService;
import br.fiap.ropz.ropz.service.RelatorioService;
import br.fiap.ropz.ropz.service.TemperaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/relatorio")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private LocalizacaoService localizacaoService;

    @Autowired
    private TemperaturaService temperaturaService;

    @GetMapping(value = "/api/temperatura/{idTemperatura}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RelatorioResponseDTO> getRelatorioByTemperaturaId(@PathVariable  Long idTemperatura) {

        Relatorio relatorio = relatorioService.findByRelatorioTemperaturaId(idTemperatura);

        if (relatorio == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        RelatorioResponseDTO relatorioResponse = relatorioService.relatorioToResponse(relatorio);

        return new ResponseEntity<>(relatorioResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/api/temperaturas/current/{idLocalizacao}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RelatorioResponseDTO>> getRelatorioTemperaturas(@PathVariable  Long idLocalizacao) {

        Localizacao localizacao = localizacaoService.findById(idLocalizacao);

        if (localizacao == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Relatorio> relatorios = relatorioService.getRelatorioOrigemCurrent(localizacao);

        if (relatorios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<RelatorioResponseDTO> relatoriosDTO = relatorios.stream().map(relatorio -> relatorioService
                .relatorioToResponse(relatorio))
                .toList();

        return new ResponseEntity<>(relatoriosDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/api/temperatura/forecast/{idLocalizacao}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RelatorioResponseDTO> getMaiorTemperaturaAFrente(@PathVariable  Long idLocalizacao) {

        Localizacao localizacao = localizacaoService.findById(idLocalizacao);

        Relatorio relatorio = relatorioService.getRelatorioOrigemForecast(localizacao);

        if (relatorio == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        RelatorioResponseDTO relatorioResponse = relatorioService.relatorioToResponse(relatorio);

        return new ResponseEntity<>(relatorioResponse, HttpStatus.OK);
    }

}
