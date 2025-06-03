package br.fiap.ropz.ropz.controller;

import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.service.LocalizacaoService;
import br.fiap.ropz.ropz.service.TemperaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/temperatura")
public class TemperaturaController {

    @Autowired
    private LocalizacaoService localizacaoService;

    @Autowired
    private TemperaturaService temperaturaService;

    @GetMapping(value = "/api/current/{idLocalizacao}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTemperaturaByLocal(@PathVariable Long idLocalizacao) {

        Localizacao localizacao = localizacaoService.findById(idLocalizacao);

        temperaturaService.consultarTemperaturaAtual(localizacao);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/api/forecast/{idLocalizacao}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAlertaFuturoByLocal(@PathVariable Long idLocalizacao) {

        Localizacao localizacao = localizacaoService.findById(idLocalizacao);

        temperaturaService.consultarMaiorPrevisao(localizacao);

        return ResponseEntity.noContent().build();
    }
}
