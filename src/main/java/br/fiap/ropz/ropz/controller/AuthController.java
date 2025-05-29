package br.fiap.ropz.ropz.controller;

import br.fiap.ropz.ropz.dto.usuario.UsuarioRequestDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AuthController {

    @GetMapping("/cadaster")
    public String cadastrarUsuario(Model model) {

        model.addAttribute("usuarioRequest", new UsuarioRequestDTO());

        return "cadastro";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
