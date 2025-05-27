package br.fiap.ropz.ropz.controller;

import br.fiap.ropz.ropz.dto.usuario.UsuarioRequestDTO;
import br.fiap.ropz.ropz.dto.usuario.UsuarioResponseDTO;
import br.fiap.ropz.ropz.model.usuario.Usuario;
import br.fiap.ropz.ropz.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public String save(@Valid @ModelAttribute UsuarioRequestDTO userRequestDTO) {
        log.info("Recebida requisição de registro via formulário para usuário: {}", userRequestDTO.getEmail());

        usuarioService.save(userRequestDTO);

        return "redirect:/";
    }

    @PostMapping(value = "/api/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> saveApi(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        log.info("Recebida requisição POST /api/ para salvar usuário: {}", usuarioRequestDTO.getEmail());

        Usuario usuario = usuarioService.save(usuarioRequestDTO);
        UsuarioResponseDTO usuarioResponseDTO = usuarioService.usuarioResponse(usuario);

        log.info("Usuário salvo com sucesso: ID {}", usuario.getId());

        return new ResponseEntity<>(usuarioResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping(value = "/api/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Long idUsuario) {
        log.info("Recebida requisição GET /api/{} para buscar usuário", idUsuario);

        Usuario usuario = usuarioService.getById(idUsuario);

        if (usuario == null) {
            log.warn("Usuário com ID {} não encontrado", idUsuario);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.info("Usuário encontrado: {}", usuario.getCredenciais().getEmail());

        UsuarioResponseDTO usuarioResponseDTO = usuarioService.usuarioResponse(usuario);

        return new ResponseEntity<>(usuarioResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteById(@PathVariable Long idUsuario) {
        log.info("Recebida requisição Delete /api/{} para deletar usuário", idUsuario);

        Usuario usuario = usuarioService.getById(idUsuario);

        if (usuario == null) {
            log.warn("Usuário com ID {} não encontrado", idUsuario);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.info("Usuário encontrado: {}", usuario.getCredenciais().getEmail());
        usuarioService.deleteById(idUsuario);

        return new ResponseEntity<>("", HttpStatus.OK);
    }
}

