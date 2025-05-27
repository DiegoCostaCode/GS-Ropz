package br.fiap.ropz.ropz.model.usuario;

import br.fiap.ropz.ropz.model.Credenciais;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDetails implements UserDetails {

    private Credenciais credenciais;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + credenciais.getTipo().name()));
    }

    @Override
    public String getPassword() {
        return credenciais.getSenha();
    }

    @Override
    public String getUsername() {
        return credenciais.getEmail();
    }

    public Usuario getUsuario() {
        return credenciais.getUsuario();
    }
}
