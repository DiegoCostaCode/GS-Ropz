package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.dto.usuario.UsuarioRequestDTO;
import br.fiap.ropz.ropz.dto.usuario.UsuarioResponseDTO;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.usuario.Usuario;
import br.fiap.ropz.ropz.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CredenciaisService credenciaisService;

    @Autowired
    private LocalizacaoService localizacaoService;

    @Transactional
    public Usuario save(UsuarioRequestDTO userRequestDTO){
        log.info("Salvando usuário: {}", userRequestDTO.getNome());
        Usuario usuario = new Usuario();

        usuario.setNome(userRequestDTO.getNome());
        usuario.setTelefone(userRequestDTO.getTelefone());

        Localizacao localizacao = localizacaoService.buscarPorCep(userRequestDTO.getCep());
        usuario.setLocalizacao(localizacao);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        credenciaisService.save(userRequestDTO.getEmail(), userRequestDTO.getSenha(), usuarioSalvo);

        log.info("Usuario criado! {} ", usuarioSalvo.getId());
        return usuarioSalvo;
    }

    public Usuario getById(Long idUsuario) {
        log.info("Buscando usuário por ID: {}", idUsuario);
        return usuarioRepository.findById(idUsuario).orElse(null);
    }

    public Usuario getByEmail(String email) {
        log.info("Realizando a busca usuário por email: {}", email);
        return usuarioRepository.findByCredenciaisEmail(email).orElse(null);
    }

    public UsuarioResponseDTO usuarioResponse(Usuario usuario) {
        log.info("Convertendo usuário para DTO: {}", usuario.getNome());

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCredenciais().getEmail(),
                usuario.getTelefone(),
                usuario.getLocalizacao().getCep(),
                usuario.getCredenciais().getDataCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
    }

    public void deleteById(Long idUsuario) {
        log.info("Deletando usuário com ID: {}", idUsuario);

        try {
            usuarioRepository.deleteById(idUsuario);
            log.info("Usuário com ID {} deletado com sucesso", idUsuario);
        } catch (Exception e) {
            log.error("Erro ao deletar usuário com ID {}: {}", idUsuario, e.getMessage());
        }
    }
}
