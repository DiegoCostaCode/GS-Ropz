package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.dto.usuario.UsuarioRequestDTO;
import br.fiap.ropz.ropz.dto.usuario.UsuarioResponseDTO;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.Usuario;
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

    @Transactional
    public Usuario update(Usuario usuario, UsuarioRequestDTO userRequestDTO){
        log.info("Atualizando usuário: {}", userRequestDTO.getNome());

        usuario.setNome(userRequestDTO.getNome());
        usuario.setTelefone(userRequestDTO.getTelefone());

        Localizacao localizacao = localizacaoService.buscarPorCep(userRequestDTO.getCep());
        usuario.setLocalizacao(localizacao);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        credenciaisService.update(userRequestDTO.getEmail(), userRequestDTO.getSenha(), usuarioSalvo);

        log.info("Usuário atualizado! ID: {}", usuarioSalvo.getId());
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

    public UsuarioRequestDTO usuarioRequestDTO(Long Idusuario) {

        Usuario usuario = getById(Idusuario);

        UsuarioRequestDTO usuarioRequestDTO = new UsuarioRequestDTO();

        usuarioRequestDTO.setNome(usuario.getNome());
        usuarioRequestDTO.setTelefone(usuario.getTelefone());
        usuarioRequestDTO.setSenha("");
        usuarioRequestDTO.setEmail(usuario.getCredenciais().getEmail());
        usuarioRequestDTO.setCep(usuario.getLocalizacao().getCep());

        return usuarioRequestDTO;
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
