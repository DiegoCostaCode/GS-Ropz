package br.fiap.ropz.ropz.controller;

import br.fiap.ropz.ropz.dto.usuario.UsuarioRequestDTO;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.temperatura.Temperatura;
import br.fiap.ropz.ropz.model.Usuario;
import br.fiap.ropz.ropz.model.relatorio.Relatorio;
import br.fiap.ropz.ropz.model.usuario.UsuarioDetails;
import br.fiap.ropz.ropz.service.RelatorioService;
import br.fiap.ropz.ropz.service.TemperaturaService;
import br.fiap.ropz.ropz.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class HomeController {

    private final TemperaturaService temperaturaService;
    private final RelatorioService relatorioService;
    private final UsuarioService usuarioService;

    public HomeController(TemperaturaService temperaturaService, RelatorioService relatorioService, UsuarioService usuarioService) {
        this.temperaturaService = temperaturaService;
        this.relatorioService = relatorioService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UsuarioDetails user, Model model) {

        Usuario usuario = user.getUsuario();

        UsuarioRequestDTO usuarioRequest =  usuarioService.usuarioRequestDTO(usuario.getId());

        Localizacao localizacao = usuario.getLocalizacao();

        Temperatura temperatura = temperaturaService.consultarTemperaturaAtual(localizacao);

        Relatorio relatorio = relatorioService.findByRelatorioTemperaturaId(temperatura.getId());
        List<Relatorio> relatorios =  relatorioService.getRelatorioOrigemCurrent(localizacao);
        Relatorio relatorioForecast = relatorioService.getRelatorioOrigemForecast(localizacao);

        model.addAttribute("idLocalizacao", localizacao.getId());
        model.addAttribute("cidade", localizacao.getCidade());
        model.addAttribute("estado", localizacao.getEstado());
        model.addAttribute("lat", localizacao.getLatitude());
        model.addAttribute("long", localizacao.getLongitude());

        model.addAttribute("usuarioRequestDTO", usuarioRequest);

        model.addAttribute("relatorioTempAtual", relatorio);
        model.addAttribute("historicoTemperatura", relatorios);
        model.addAttribute("temperaturaAlta", relatorioForecast);

        return "home";
    }

}
