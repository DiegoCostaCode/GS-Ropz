package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.model.usuario.Credenciais;
import br.fiap.ropz.ropz.model.usuario.EnumTipoUsuario;
import br.fiap.ropz.ropz.model.Usuario;
import br.fiap.ropz.ropz.repository.CredenciaisRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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

        if(passwordEncoder.matches(senha, credenciais.getSenha())) {
            credenciais.setSenha(senha);
        } else {
            credenciais.setSenha(passwordEncoder.encode(senha));
        }

        credenciais.setUsuario(usuario);
        credenciais.setDataCadastro(LocalDateTime.now());
        credenciais.setTipo(EnumTipoUsuario.DEFAULT);
        usuario.setCredenciais(credenciais);

        return credenciaisRepository.save(credenciais);
    }

    public Credenciais findByUsuario(Usuario usuario) {
        return credenciaisRepository.findCredenciaisByUsuario(usuario).orElse(null);
    }

    @Transactional
    public Credenciais update(String email, String senha, Usuario usuario) {
        log.info("Atualizando credenciais para usuário: {}", usuario.getNome());

        Credenciais credenciais = findByUsuario(usuario);

        if (credenciais == null) {
            throw new IllegalArgumentException("Credenciais não encontradas para o usuário: " + usuario.getNome());
        }

        credenciais.setEmail(email);

        if (senha != null && !senha.isBlank() && !passwordEncoder.matches(senha, credenciais.getSenha())) {
            credenciais.setSenha(passwordEncoder.encode(senha));
        } else if (senha != null && !senha.isBlank() && passwordEncoder.matches(senha, credenciais.getSenha())) {
            credenciais.setSenha(usuario.getCredenciais().getSenha());
        } else if (senha == null || senha.isBlank()) {
            throw new BadCredentialsException("Senha inválida!");
        }

        return credenciaisRepository.save(credenciais);
    }
}
