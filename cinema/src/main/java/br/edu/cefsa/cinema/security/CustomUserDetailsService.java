package br.edu.cefsa.cinema.security;

import java.util.Set;

import javax.management.relation.Role;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.edu.cefsa.cinema.model.Usuario;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Aqui você consultaria um repositório real. Exemplo fixo para fins didáticos:
        if ("admin".equals(username)) {
            Usuario user = new Usuario("admin", "$2a$10$ExemploCriptografado...", Set.of(Role.ADMIN, Role.LOGADO));
            return new CustomUserDetails(user);
        } else if ("user".equals(username)) {
            Usuario user = new Usuario("user", "$2a$10$OutroExemplo...", Set.of(Role.LOGADO));
            return new CustomUserDetails(user);
        }
        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }
}