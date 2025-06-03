package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.dto.localizacao.LocalizacaoResponseDTO;
import br.fiap.ropz.ropz.dto.usuario.UsuarioRequestDTO;
import br.fiap.ropz.ropz.dto.usuario.UsuarioResponseDTO;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.Usuario;
import br.fiap.ropz.ropz.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CredenciaisService credenciaisService;

    @Autowired
    private LocalizacaoService localizacaoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public Usuario update(Long idUsuario, UsuarioRequestDTO userRequestDTO){
        log.info("Atualizando usuário: {}", userRequestDTO.getEmail());

        Usuario usuario = getById(idUsuario);

        usuario.setNome(userRequestDTO.getNome());
        usuario.setTelefone(userRequestDTO.getTelefone());

        Localizacao localizacao = localizacaoService.buscarPorCep(userRequestDTO.getCep());
        usuario.setLocalizacao(localizacao);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        credenciaisService.update(userRequestDTO.getEmail(), userRequestDTO.getSenha(), usuarioSalvo);

        log.info("Usuário atualizado! ID: [ {} ]", usuarioSalvo.getId());
        return usuarioSalvo;
    }

    public Usuario getById(Long idUsuario) {
        log.info("Buscando usuário por ID: [ {} ]", idUsuario);

        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID"));
    }

    public Usuario getByEmail(String email) {
        log.info("Realizando a busca usuário por email: {}", email);

        Optional<Usuario> usuario = usuarioRepository.findByCredenciaisEmail(email);

        if (usuario.isEmpty()) {
            log.warn("Usuário com email {} não encontrado", email);
            return null;
        }

        log.info("Usuário encontrado: {}", usuario.get().getNome());

        return usuario.get();
    }

    public UsuarioResponseDTO usuarioResponse(Usuario usuario) {

        LocalizacaoResponseDTO localizacaoResponseDTO = localizacaoService.localizacaoResponseDTO(usuario.getLocalizacao());

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCredenciais().getEmail(),
                usuario.getTelefone(),
                localizacaoResponseDTO,
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
        log.info("Deletando usuário com ID: [ {} ]", idUsuario);

        try {
            usuarioRepository.deleteById(idUsuario);
            log.info("Usuário com ID: [ {} ] - Deletado com sucesso!", idUsuario);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao deletar usuário com ID: [ {} ] - {}", idUsuario, e.getMessage());
        }
    }

    public Usuario authenticate(String email, String senha) {
        log.info("Autenticando usuário email: [ {} ]", email);

        Usuario usuario = getByEmail(email);

        if (usuario == null) {
            log.info("Usuário email: [ {} ] - Não encontrado", email);
            throw new UsernameNotFoundException("Email não encontrado");
        }

        if (!passwordEncoder.matches(senha, usuario.getCredenciais().getSenha())) {
            log.info("Senha inválida! Email: [ {} ]", email);
            throw new BadCredentialsException("Senha inválida");
        }

        return usuario;
    }
}
