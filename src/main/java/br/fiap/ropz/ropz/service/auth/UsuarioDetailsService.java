package br.fiap.ropz.ropz.service.auth;

import br.fiap.ropz.ropz.model.Usuario;
import br.fiap.ropz.ropz.model.usuario.UsuarioDetails;
import br.fiap.ropz.ropz.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioDetailsService.class);

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Buscando usuário por email: [ {} ]", email);
        Usuario usuario = usuarioService.getByEmail(email);

        if (usuario == null) {
            log.warn("Usuário não encontrado com o email: [ {} ]", email);
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
        }

        return new UsuarioDetails(usuario.getCredenciais());
    }
}
