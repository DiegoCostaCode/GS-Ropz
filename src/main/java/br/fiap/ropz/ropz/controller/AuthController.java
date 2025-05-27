package br.fiap.ropz.ropz.controller;

import br.fiap.ropz.ropz.dto.usuario.UsuarioRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AuthController {

    @GetMapping("/")
    public String cadastrarUsuario(Model model) {

        model.addAttribute("usuarioRequest", new UsuarioRequestDTO());

        return "cadastro";
    }
}
