package br.fiap.ropz.ropz.controller;

import br.fiap.ropz.ropz.model.Temperatura;
import br.fiap.ropz.ropz.model.usuario.Usuario;
import br.fiap.ropz.ropz.model.usuario.UsuarioDetails;
import br.fiap.ropz.ropz.service.TemperaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private TemperaturaService temperaturaService;


    @GetMapping("/")
    public String home(@AuthenticationPrincipal UsuarioDetails user, Model model) {

        Usuario usuario = user.getUsuario();
        Temperatura temperatura = temperaturaService.consultarTemperaturaAtual(usuario.getLocalizacao());
        List<Temperatura> historicoTemperaturas = temperaturaService.getHistoricoTemperaturas(usuario.getLocalizacao().getId());

        model.addAttribute("cidade", usuario.getLocalizacao().getCidade());
        model.addAttribute("estado", usuario.getLocalizacao().getEstado());
        model.addAttribute("lat", usuario.getLocalizacao().getLatitude());
        model.addAttribute("long", usuario.getLocalizacao().getLongitude());
        model.addAttribute("nome", usuario.getNome());
        model.addAttribute("telefone", usuario.getTelefone());
        model.addAttribute("email", usuario.getCredenciais().getEmail());
        model.addAttribute("cep", usuario.getLocalizacao().getCep());

        model.addAttribute("temperaturaAtual", temperatura);
        model.addAttribute("historicoTemperatura", historicoTemperaturas);

        return "home";
    }

}
