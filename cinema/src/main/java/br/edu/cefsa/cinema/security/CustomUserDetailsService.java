package br.edu.cefsa.cinema.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.repository.UsuarioRepository; 

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String apelido) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByApelido(apelido)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com apelido: " + apelido));

        return new CustomUserDetails(usuario);
    }
}