package br.edu.cefsa.cinema.controller;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import br.edu.cefsa.cinema.Enum.Role;
import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Para pegar o admin logado
import org.springframework.security.crypto.password.PasswordEncoder; // Para caso precise resetar senha
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import br.edu.cefsa.cinema.security.CustomUserDetails; // Para o @AuthenticationPrincipal

@Controller
@RequestMapping("/administracao") // Todas as rotas aqui são prefixadas com /administracao
@PreAuthorize("hasRole('ADMIN')") // Garante que apenas usuários com ROLE_ADMIN acessem este controller
public class AdminUsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // @Autowired // Descomente se precisar resetar senhas aqui
    // private PasswordEncoder passwordEncoder;

    /**
     * Mapeia a rota GET /administracao/lista-usuarios.
     * Busca todos os usuários e os envia para a view de listagem.
     * @param model Objeto para passar dados para a view.
     * @return O nome do template Thymeleaf para listar usuários.
     */
    @GetMapping("/lista-usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll()); // Busca todos os usuários
        return "administracao/lista-usuarios"; // Retorna o template
    }

    /**
     * Mapeia a rota GET /administracao/editar/{id} para exibir o formulário de edição de um usuário pelo admin.
     * @param id O UUID do usuário a ser editado.
     * @param model Objeto para passar dados para a view.
     * @return O nome do template Thymeleaf para editar usuário, ou redireciona se o usuário não for encontrado.
     */
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable UUID id, Model model) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            // Adicionar mensagem de erro com RedirectAttributes seria melhor
            return "redirect:/administracao/lista-usuarios?erroUsuarioNaoEncontrado";
        }

        model.addAttribute("usuario", usuarioOptional.get()); // Usuário a ser editado
        model.addAttribute("rolesDisponiveis", Arrays.asList(Role.values())); // Lista de todas as roles para o <select>
        return "administracao/editar-usuario"; // Template de edição
    }

    /**
     * Mapeia a rota POST /administracao/editar/{id} para processar a atualização de um usuário pelo admin.
     * Inclui lógica para não permitir alteração de role de admins protegidos e validação de apelido.
     * @param id O UUID do usuário sendo editado.
     * @param usuarioAtualizado Objeto Usuario com os novos dados vindos do formulário.
     * @param model Objeto para passar dados de volta para a view em caso de erro.
     * @param userDetails Detalhes do administrador autenticado (para a lógica de proteção).
     * @return Redireciona para a lista de usuários em caso de sucesso, ou volta para a página de edição com erro.
     */
    @PostMapping("/editar/{id}")
    public String salvarEdicaoUsuario(@PathVariable UUID id,
                                      @ModelAttribute Usuario usuarioAtualizado,
                                      Model model,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) { // Admin logado

        Optional<Usuario> usuarioExistenteOptional = usuarioRepository.findById(id);
        if (usuarioExistenteOptional.isEmpty()) {
            return "redirect:/administracao/lista-usuarios?erroUsuarioNaoEncontrado";
        }
        Usuario usuarioExistente = usuarioExistenteOptional.get();

        // Lógica de proteção para não alterar roles de admins específicos
        boolean isProtectedAdmin = (usuarioExistente.getApelido().equals("Kastarys") || usuarioExistente.getApelido().equals("Kaulingames")) &&
                usuarioExistente.getRoles().contains(Role.ADMIN);

        // Verifica se o novo apelido já está em uso por OUTRO usuário
        Optional<Usuario> usuarioComMesmoApelido = usuarioRepository.findByApelido(usuarioAtualizado.getApelido());
        if (usuarioComMesmoApelido.isPresent() && !usuarioComMesmoApelido.get().getIdPadrao().equals(id)) {
            // Mantém o ID original do usuário sendo editado no objeto que volta para o formulário
            usuarioAtualizado.setIdPadrao(id);
            // Se for um admin protegido, não envia as roles atualizadas do formulário, mas as originais
            if(isProtectedAdmin) usuarioAtualizado.setRoles(usuarioExistente.getRoles());

            model.addAttribute("erroApelido", "Este apelido já está em uso por outro usuário.");
            model.addAttribute("usuario", usuarioAtualizado); // Retorna os dados (possivelmente alterados) para o formulário
            model.addAttribute("rolesDisponiveis", Arrays.asList(Role.values()));
            return "administracao/editar-usuario"; // Volta para a página de edição
        }

        // Atualiza os dados do usuário
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setApelido(usuarioAtualizado.getApelido());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());

        // Aplica as roles apenas se não for um admin protegido ou se o admin logado não for um dos protegidos tentando se auto-modificar
        // A lógica no Thymeleaf já esconde o campo, esta é uma dupla checagem.
        if (!isProtectedAdmin) {
            usuarioExistente.setRoles(usuarioAtualizado.getRoles());
        }
        // Se for um admin protegido, as roles não são alteradas (permanecem as de usuarioExistente).

        usuarioRepository.save(usuarioExistente); // Salva as alterações
        return "redirect:/administracao/lista-usuarios"; // Redireciona para a lista
    }
}