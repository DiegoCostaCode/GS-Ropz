package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.dto.usuario.UsuarioRequestDTO;
import br.fiap.ropz.ropz.model.Localizacao;
import br.fiap.ropz.ropz.model.Usuario;
import br.fiap.ropz.ropz.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Usuario save(UsuarioRequestDTO userRequestDTO)
    {
        log.info("Salvando usuário: {}", userRequestDTO.getNome());
        Usuario usuario = new Usuario();

        usuario.setNome(userRequestDTO.getNome());
        usuario.setTelefone(userRequestDTO.getTelefone());


        Localizacao localizacao = localizacaoService.buscarPorCep(userRequestDTO.getCep());
        usuario.setLocalizacao(localizacao);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        credenciaisService.save(userRequestDTO.getEmail(), userRequestDTO.getSenha(), usuarioSalvo);

        return usuarioSalvo;
    }

}
