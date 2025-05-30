package br.fiap.ropz.ropz.controller;

import br.fiap.ropz.ropz.dto.relatorio.RelatoriosServiceDTO;
import br.fiap.ropz.ropz.dto.usuario.UsuarioRequestDTO;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.Usuario;
import br.fiap.ropz.ropz.model.usuario.UsuarioDetails;
import br.fiap.ropz.ropz.service.RelatorioService;
import br.fiap.ropz.ropz.service.TemperaturaService;
import br.fiap.ropz.ropz.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TemperaturaService temperaturaService;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UsuarioDetails user, Model model) {

        Usuario usuario = user.getUsuario();
        UsuarioRequestDTO usuarioRequest = usuarioService.usuarioRequestDTO(usuario.getId());

        Localizacao localizacao = usuario.getLocalizacao();

        temperaturaService.consultarTemperaturaAtual(localizacao);
        temperaturaService.consultarMaiorPrevisao(localizacao);

        RelatoriosServiceDTO relatorios = relatorioService.getRelatorios(localizacao);

        model.addAttribute("idLocalizacao", localizacao.getId());
        model.addAttribute("cidade", localizacao.getCidade());
        model.addAttribute("estado", localizacao.getEstado());
        model.addAttribute("lat", localizacao.getLatitude());
        model.addAttribute("long", localizacao.getLongitude());

        model.addAttribute("usuarioRequestDTO", usuarioRequest);

        model.addAttribute("relatorioTempAtual", relatorios.relatorioMaisRecente());
        model.addAttribute("historicoTemperatura", relatorios.relatoriosCurrent());
        model.addAttribute("relatorioTempForecast", relatorios.relatorioForecast());

        return "home";
    }

}
