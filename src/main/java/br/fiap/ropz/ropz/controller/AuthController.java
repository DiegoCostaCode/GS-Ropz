package br.fiap.ropz.ropz.controller;

import br.fiap.ropz.ropz.config.jwt.JwtTokenUtil;
import br.fiap.ropz.ropz.dto.jwtTokenDto.ValidUserDTO;
import br.fiap.ropz.ropz.dto.usuario.LoginDTO;
import br.fiap.ropz.ropz.dto.usuario.UsuarioRequestDTO;
import br.fiap.ropz.ropz.dto.usuario.UsuarioResponseDTO;
import br.fiap.ropz.ropz.model.Usuario;
import br.fiap.ropz.ropz.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/")
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("api/login")
    public ResponseEntity<ValidUserDTO> login(@Valid @RequestBody LoginDTO loginRequest) {

        Usuario usuario = usuarioService.authenticate(loginRequest.email(), loginRequest.senha());

        UsuarioResponseDTO usuarioResponseDTO = usuarioService.usuarioResponse(usuario);
        String token = jwtTokenUtil.generateToken(usuario.getCredenciais().getEmail());

        ValidUserDTO validUserDTO = new ValidUserDTO(usuarioResponseDTO, token);

        return ResponseEntity.ok(validUserDTO);
    }
}
