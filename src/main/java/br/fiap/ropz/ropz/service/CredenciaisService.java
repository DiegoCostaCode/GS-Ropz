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

        log.info("Salvando credenciais para usuário e-mail: [ {} ]", email);

        Credenciais credenciais = new Credenciais();

        credenciais.setEmail(email);
        credenciais.setSenha(passwordEncoder.encode(senha));
        credenciais.setUsuario(usuario);
        credenciais.setDataCadastro(LocalDateTime.now());
        credenciais.setTipo(EnumTipoUsuario.DEFAULT);
        usuario.setCredenciais(credenciais);

        try{
            Credenciais credenciaisSalvas = credenciaisRepository.save(credenciais);
            log.info("Credenciais salvas com sucesso! Usuário ID: [ {} ]", usuario.getId());
            return credenciaisSalvas;
        } catch (RuntimeException e) {
            log.error("Erro ao salvar credenciais usuário ID: [ {} ]", usuario.getId(), e);
            throw new RuntimeException(e);
        }

    }

    public Credenciais findByUsuario(Usuario usuario) {
        return credenciaisRepository.findCredenciaisByUsuario(usuario).orElse(null);
    }

    @Transactional
    public Credenciais update(String email, String senha, Usuario usuario) {
        log.info("Atualizando credenciais para usuário: [ {} ]", usuario.getCredenciais().getEmail());

        Credenciais credenciais = findByUsuario(usuario);

        credenciais.setEmail(email);

        if (senha != null && !senha.isBlank() && !passwordEncoder.matches(senha, credenciais.getSenha())) {
            log.info("Senha alterada para usuário: [ {} ]", email);
            credenciais.setSenha(passwordEncoder.encode(senha));
        } else if (senha != null && !senha.isBlank() && passwordEncoder.matches(senha, credenciais.getSenha())) {
            log.info("Senha mantida: [ {} ]", email);
            credenciais.setSenha(usuario.getCredenciais().getSenha());
        } else if (senha == null || senha.isBlank()) {
            log.warn("Senha não informada ou inválida para usuário: [ {} ]", email);
            throw new BadCredentialsException("Senha inválida!");
        }

        try{
            Credenciais credenciaisAtualizadas = credenciaisRepository.save(credenciais);
            log.info("Credenciais atualizadas com sucesso! Usuário ID: [ {} ]", usuario.getId());
            return credenciaisAtualizadas;
        } catch (RuntimeException e) {
            log.error("Erro ao atualizar credenciais usuário ID: [ {} ]", usuario.getId(), e);
            throw new RuntimeException(e);
        }
    }
}
