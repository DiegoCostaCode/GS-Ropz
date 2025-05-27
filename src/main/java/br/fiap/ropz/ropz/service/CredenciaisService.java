package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.model.Credenciais;
import br.fiap.ropz.ropz.model.usuario.Enum_tipo_usuario;
import br.fiap.ropz.ropz.model.usuario.Usuario;
import br.fiap.ropz.ropz.repository.CredenciaisRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CredenciaisService {

    private static final Logger log = LoggerFactory.getLogger(CredenciaisService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CredenciaisRepository credenciaisRepository;

    @Transactional
    public Credenciais save(String email, String senha, Usuario usuario) {

        log.info("Salvando credenciais para usuário: {}", usuario.getNome());

        Credenciais credenciais = new Credenciais();

        credenciais.setEmail(email);
        credenciais.setSenha(passwordEncoder.encode(senha));
        credenciais.setUsuario(usuario);
        credenciais.setDataCadastro(LocalDateTime.now());
        credenciais.setTipo(Enum_tipo_usuario.DEFAULT);
        usuario.setCredenciais(credenciais);

        return credenciaisRepository.save(credenciais);
    }
}
