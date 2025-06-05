package br.edu.cefsa.cinema.controller;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Para mensagens flash

import br.edu.cefsa.cinema.Enum.Role;
import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.repository.UsuarioRepository;
import br.edu.cefsa.cinema.security.CustomUserDetails;
import br.edu.cefsa.cinema.service.UsuarioService; // Assumindo que este serviço existe para salvar
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/usuarios") // Define que todas as rotas neste controller começarão com /usuarios
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService; // Serviço para lógica de usuário (ex: salvar)

    @Autowired
    private PasswordEncoder passwordEncoder; // Para codificar senhas

    @Autowired
    private UsuarioRepository usuarioRepository; // Para buscar usuários

    /**
     * Mapeia a rota POST /usuarios/cadastrar para processar o formulário de cadastro.
     * Verifica se o apelido já existe. Se não, salva o novo usuário com senha codificada e role padrão.
     * @param usuario Objeto Usuario populado com os dados do formulário.
     * @param model Objeto para passar dados de volta para a view em caso de erro.
     * @return Redireciona para a página de login em caso de sucesso, ou volta para a página de cadastro com erro.
     */
    @PostMapping("/cadastrar")
    public String cadastro(@ModelAttribute Usuario usuario, Model model, RedirectAttributes redirectAttributes) {
        // Verifica se o apelido (username) já está em uso
        if (usuarioRepository.findByApelido(usuario.getApelido()).isPresent()) {
            model.addAttribute("erroApelido", true); // Adiciona flag de erro para Thymeleaf
            model.addAttribute("usuario", usuario); // Retorna os dados já preenchidos (exceto senha)
            return "usuarios/cadastro"; // Volta para a página de cadastro
        }

        // Codifica a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        // Define a role padrão para novos usuários
        usuario.setRoles(Set.of(Role.LOGADO));
        // Salva o usuário usando o serviço (que usa o repositório)
        usuarioService.salvarUsuario(usuario);

        redirectAttributes.addFlashAttribute("cadastroSucesso", "Cadastro realizado com sucesso! Faça o login.");
        return "redirect:/usuarios/login"; // Redireciona para a página de login
    }

    /**
     * Mapeia a rota GET /usuarios/perfil para exibir a página de perfil do usuário logado.
     * @param model Objeto para passar dados para a view.
     * @param userDetails Detalhes do usuário autenticado, injetado pelo Spring Security.
     * @return O nome do template Thymeleaf para a página de perfil.
     */
    @GetMapping("/perfil")
    public String exibirPerfil(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario usuario = userDetails.getUsuario(); // Obtém o objeto Usuario do principal autenticado
        model.addAttribute("usuario", usuario); // Adiciona o usuário ao modelo
        return "usuarios/perfil"; // Retorna o template de perfil
    }

    /**
     * Mapeia a rota GET /usuarios/editar/{id} para exibir o formulário de edição do perfil do próprio usuário.
     * Garante que o usuário logado só possa acessar a página de edição do seu próprio perfil.
     * @param id O ID do usuário a ser editado (deve ser o mesmo do usuário logado).
     * @param model Objeto para passar dados para a view.
     * @param userDetails Detalhes do usuário autenticado.
     * @return O nome do template Thymeleaf para editar perfil, ou redireciona se houver tentativa de acesso indevido.
     */
    @GetMapping("/editar/{id}")
    public String editarPerfil(@PathVariable UUID id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario usuarioLogado = userDetails.getUsuario();

        // Medida de segurança: Garante que o usuário logado só pode editar seu próprio perfil
        if (!usuarioLogado.getIdPadrao().equals(id)) {
            // Idealmente, redirecionar para uma página de acesso negado ou logar a tentativa
            return "redirect:/erro"; // Ou para a home "/"
        }

        model.addAttribute("usuario", usuarioLogado); // Passa o usuário para o formulário
        return "usuarios/editar"; // Retorna o template de edição
    }

    /**
     * Mapeia a rota POST /usuarios/editar/{id} para processar a atualização do perfil do usuário.
     * Permite que o usuário atualize nome, apelido e email. Não permite alteração de senha aqui.
     * @param id O ID do usuário sendo editado.
     * @param usuarioAtualizado Objeto Usuario com os novos dados vindos do formulário.
     * @param userDetails Detalhes do usuário autenticado.
     * @param model Objeto para passar dados de volta para a view em caso de erro.
     * @return Redireciona para a página de perfil em caso de sucesso, ou volta para a página de edição com erro.
     */
    @PostMapping("/editar/{id}")
    public String salvarEdicaoPerfil(@PathVariable UUID id, @ModelAttribute Usuario usuarioAtualizado,
                                     @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        Optional<Usuario> optional = usuarioRepository.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/erro"; // Usuário não encontrado
        }
        Usuario usuarioExistente = optional.get();

        // Segurança: Confirma que o usuário logado está editando seu próprio perfil
        if (!usuarioExistente.getIdPadrao().equals(userDetails.getUsuario().getIdPadrao())) {
            return "redirect:/erro"; // Tentativa de edição não autorizada
        }

        // Verifica se o novo apelido já está em uso por OUTRO usuário
        Optional<Usuario> apelidoExistente = usuarioRepository.findByApelido(usuarioAtualizado.getApelido());
        if (apelidoExistente.isPresent() && !apelidoExistente.get().getIdPadrao().equals(usuarioExistente.getIdPadrao())) {
            model.addAttribute("erroApelido", true); // Flag para exibir erro no Thymeleaf
            // Retorna o objeto usuarioAtualizado para repopular o formulário, mas com o ID correto do usuário sendo editado
            // É importante manter os dados que não podem ser alterados (como roles, ID) do usuarioExistente
            usuarioAtualizado.setIdPadrao(usuarioExistente.getIdPadrao());
            usuarioAtualizado.setRoles(usuarioExistente.getRoles()); // Mantém as roles originais
            model.addAttribute("usuario", usuarioAtualizado);
            return "usuarios/editar"; // Volta para a página de edição
        }

        // Atualiza os campos permitidos
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setApelido(usuarioAtualizado.getApelido());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        // A senha não é alterada nesta tela. Roles também não.

        usuarioRepository.save(usuarioExistente); // Salva as alterações

        // Importante: Atualiza os dados do usuário na sessão de segurança atual
        // para que a navbar e outras partes do site reflitam a mudança imediatamente.
        userDetails.getUsuario().setNome(usuarioExistente.getNome());
        userDetails.getUsuario().setApelido(usuarioExistente.getApelido());
        userDetails.getUsuario().setEmail(usuarioExistente.getEmail());

        return "redirect:/usuarios/perfil"; // Redireciona para o perfil atualizado
    }

    /**
     * Mapeia a rota POST /usuarios/excluir/{id} para excluir a conta do próprio usuário.
     * Garante que o usuário logado só possa excluir sua própria conta e que não seja um ADMIN protegido.
     * Faz o logout do usuário após a exclusão.
     * @param id O ID do usuário a ser excluído.
     * @param userDetails Detalhes do usuário autenticado.
     * @param request Objeto HttpServletRequest para realizar o logout.
     * @return Redireciona para a página inicial.
     * @throws ServletException
     */
    @PostMapping("/excluir/{id}")
    public String excluirConta(@PathVariable UUID id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               HttpServletRequest request) throws ServletException { // Removido HttpServletResponse não utilizado

        Usuario usuarioLogado = userDetails.getUsuario();
        // Segurança: Só pode excluir o próprio perfil
        if (!usuarioLogado.getIdPadrao().equals(id)) {
            return "redirect:/erro";
        }
        // Segurança: Não permitir que admins protegidos (Kastarys, Kaulingames) se excluam por aqui
        // A lógica no Thymeleaf já esconde o botão, mas é uma dupla checagem.
        if (usuarioLogado.getRoles().contains(Role.ADMIN) &&
                (usuarioLogado.getApelido().equals("Kastarys") || usuarioLogado.getApelido().equals("Kaulingames"))) {
            // Idealmente, logar essa tentativa e redirecionar com mensagem de erro
            return "redirect:/usuarios/perfil?erroExclusaoAdmin";
        }

        usuarioRepository.deleteById(id); // Deleta o usuário

        // Faz o logout do usuário
        request.logout(); // Invalida a sessão do Spring Security
        // request.getSession().invalidate(); // Opcional, request.logout() geralmente cuida disso.

        return "redirect:/?contaExcluida"; // Redireciona para a home com um parâmetro de sucesso (opcional)
    }
}