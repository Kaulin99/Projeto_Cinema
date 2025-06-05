package br.edu.cefsa.cinema.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.repository.UsuarioRepository;

/**
 * Serviço que implementa UserDetailsService do Spring Security.
 * Responsável por carregar os detalhes de um usuário (pelo apelido) do banco de dados.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositório para acessar dados do usuário

    /**
     * Carrega um usuário pelo seu apelido (que é usado como username no sistema).
     * @param apelido O apelido do usuário a ser carregado.
     * @return Um objeto UserDetails (nossa implementação CustomUserDetails) contendo os dados do usuário.
     * @throws UsernameNotFoundException Se nenhum usuário for encontrado com o apelido fornecido.
     */
    @Override
    public UserDetails loadUserByUsername(String apelido) throws UsernameNotFoundException {
        // Busca o usuário no repositório pelo apelido
        Usuario usuario = usuarioRepository.findByApelido(apelido)
                // Se não encontrar, lança uma exceção padrão do Spring Security
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com apelido: " + apelido));

        // Retorna uma instância de CustomUserDetails, que envolve o objeto Usuario
        return new CustomUserDetails(usuario);
    }
}