package br.fiap.ropz.ropz.controller;

import br.fiap.ropz.ropz.dto.relatorio.RelatoriosServiceDTO;
import br.fiap.ropz.ropz.dto.usuario.UsuarioRequestDTO;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.Usuario;
import br.fiap.ropz.ropz.model.usuario.UsuarioDetails;
import br.fiap.ropz.ropz.service.LocalizacaoService;
import br.fiap.ropz.ropz.service.RelatorioService;
import br.fiap.ropz.ropz.service.TemperaturaService;
import br.fiap.ropz.ropz.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
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
    private LocalizacaoService localizacaoService;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UsuarioDetails user, Model model) {

        Usuario usuarioEncontrado = usuarioService.getById(user.getUsuario().getId());

        Localizacao localizacao = localizacaoService.findById(usuarioEncontrado.getLocalizacao().getId());

        UsuarioRequestDTO usuarioRequest = usuarioService.usuarioRequestDTO(usuarioEncontrado.getId());

        RelatoriosServiceDTO relatorios = relatorioService.getRelatorios(localizacao);

        model.addAttribute("localizacao", localizacao);
        model.addAttribute("usuarioRequestDTO", usuarioRequest);
        model.addAttribute("temperaturaAtualLocal", relatorios.relatorioMaisRecente());
        model.addAttribute("historicoLocal", relatorios.relatoriosCurrent());
        model.addAttribute("previsaoLocal", relatorios.relatorioForecast());

        return "home";
    }

}
