package br.edu.cefsa.cinema.security;

import java.util.Collection;
import java.util.UUID; // Import não utilizado diretamente, mas o ID do usuário é UUID
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.edu.cefsa.cinema.model.Usuario;

/**
 * Implementação customizada de UserDetails do Spring Security.
 * Envolve um objeto Usuario para fornecer informações de autenticação e autorização.
 */
public class CustomUserDetails implements UserDetails {
    private final Usuario usuario; // Objeto Usuario encapsulado

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Retorna o objeto Usuario completo.
     * Útil para acessar outros dados do usuário além dos definidos pela interface UserDetails.
     * @return O objeto Usuario.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Retorna as autoridades (roles/permissões) concedidas ao usuário.
     * As roles do Enum são prefixadas com "ROLE_" conforme convenção do Spring Security.
     * @return Uma coleção de GrantedAuthority.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())) // Ex: ROLE_ADMIN, ROLE_LOGADO
                .collect(Collectors.toList());
    }

    /**
     * Retorna a senha usada para autenticar o usuário.
     * @return A senha (já codificada).
     */
    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    /**
     * Retorna o nome de usuário usado para autenticar o usuário (neste caso, o apelido).
     * @return O apelido do usuário.
     */
    @Override
    public String getUsername() {
        return usuario.getApelido();
    }

    // Os métodos abaixo indicam o estado da conta. Para este projeto, todos retornam true.
    @Override
    public boolean isAccountNonExpired() {
        return true; // A conta não expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // A conta não é bloqueada (a menos que haja lógica adicional)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // As credenciais não expiram
    }

    @Override
    public boolean isEnabled() {
        return true; // A conta está habilitada
    }
}