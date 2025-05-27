package br.fiap.ropz.ropz.service;

import br.fiap.ropz.ropz.model.Credenciais;
import br.fiap.ropz.ropz.model.Usuario;
import br.fiap.ropz.ropz.repository.CredenciaisRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredenciaisService {

    @Autowired
    private CredenciaisRepository credenciaisRepository;

    @Transactional
    public Credenciais save(String email, String senha, Usuario usuario) {

        Credenciais credenciais = new Credenciais();

        credenciais.setEmail(email);
        credenciais.setSenha(senha);
        credenciais.setUsuario(usuario);

        usuario.setCredenciais(credenciais);

        return credenciaisRepository.save(credenciais);
    }
}
