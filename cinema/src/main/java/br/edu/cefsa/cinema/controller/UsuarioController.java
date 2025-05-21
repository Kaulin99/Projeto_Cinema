package br.edu.cefsa.cinema.controller;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import br.edu.cefsa.cinema.Enum.Role;
import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.repository.UsuarioRepository;
import br.edu.cefsa.cinema.security.CustomUserDetails;
import br.edu.cefsa.cinema.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Cadastro de novo usuário
    @PostMapping("/cadastrar")
    public String cadastro(@ModelAttribute Usuario usuario,Model model) {
        if (usuarioRepository.findByApelido(usuario.getApelido()).isPresent()) {
            model.addAttribute("erroApelido", true);
            model.addAttribute("usuario", usuario);
            return "usuarios/cadastro";
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setRoles(Set.of(Role.LOGADO));
        usuarioService.salvarUsuario(usuario);

        return "redirect:/usuarios/login";
    }

    @GetMapping("/perfil")
    public String exibirPerfil(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();
        model.addAttribute("usuario", usuario);
        return "usuarios/perfil";
    }

    @GetMapping("/editar/{id}")
    public String editarPerfil(@PathVariable UUID id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario usuarioLogado = userDetails.getUsuario();

        if (!usuarioLogado.getIdPadrao().equals(id)) {
            return "redirect:/erro";
        }

        model.addAttribute("usuario", usuarioLogado);
        return "usuarios/editar";
    }

    @PostMapping("/editar/{id}")
public String salvarEdicaoPerfil(@PathVariable UUID id, @ModelAttribute Usuario usuarioAtualizado,
                                 @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

    Optional<Usuario> optional = usuarioRepository.findById(id);
    if (optional.isEmpty()) {
        return "redirect:/erro";
    }

    Usuario usuario = optional.get();

    // Segurança: só permite edição do próprio perfil
    if (!usuario.getIdPadrao().equals(userDetails.getUsuario().getIdPadrao())) {
        return "redirect:/erro";
    }

    // Verifica se o apelido está em uso por outro
    Optional<Usuario> apelidoExistente = usuarioRepository.findByApelido(usuarioAtualizado.getApelido());
    if (apelidoExistente.isPresent() && !apelidoExistente.get().getIdPadrao().equals(usuario.getIdPadrao())) {
        model.addAttribute("erroApelido", true);
        model.addAttribute("usuario", usuarioAtualizado);
        return "usuarios/editar";
    }

    // Atualiza os campos
    usuario.setNome(usuarioAtualizado.getNome());
    usuario.setApelido(usuarioAtualizado.getApelido());
    usuario.setEmail(usuarioAtualizado.getEmail());

    usuarioRepository.save(usuario);

    // Atualiza os dados do usuário autenticado manualmente
    userDetails.getUsuario().setNome(usuario.getNome());
    userDetails.getUsuario().setApelido(usuario.getApelido());
    userDetails.getUsuario().setEmail(usuario.getEmail());

    return "redirect:/usuarios/perfil";
}
@PostMapping("/excluir/{id}")
public String excluirConta(@PathVariable UUID id,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           HttpServletRequest request, HttpServletResponse response) throws ServletException {
    if (!userDetails.getUsuario().getIdPadrao().equals(id)) {
        return "redirect:/erro";
    }

    usuarioRepository.deleteById(id);

    // Invalida a sessão e faz logout manual
    request.logout(); // isso chama o LogoutFilter do Spring Security
    request.getSession().invalidate();

    return "redirect:/"; // redireciona para a home (ou página de sucesso)
}



}
