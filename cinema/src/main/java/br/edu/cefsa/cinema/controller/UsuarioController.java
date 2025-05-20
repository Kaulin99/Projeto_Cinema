package br.edu.cefsa.cinema.controller;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.edu.cefsa.cinema.Enum.Role;
import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.repository.UsuarioRepository;
import br.edu.cefsa.cinema.security.CustomUserDetails;
import br.edu.cefsa.cinema.service.UsuarioService;


@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("cadastrar")
    public String cadastro(@ModelAttribute Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setRoles(Set.of(Role.LOGADO));
        usuarioService.salvarUsuario(usuario);
        return "redirect:/usuarios/login";  // redireciona para a tela de login
    }

    @GetMapping("/perfil")
    public String exibirPerfil(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario usuario = userDetails.getUsuario(); // supondo que CustomUserDetails tenha getUsuario()
        model.addAttribute("usuario", usuario);
        return "usuarios/perfil"; // caminho do HTML (por exemplo: templates/usuario/perfil.html)
    }

    @GetMapping("/editar/{id}")
    public String editarPerfil(@PathVariable UUID id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario usuarioLogado = userDetails.getUsuario();

        // Garantir que o usuário só edite a si mesmo
        if (!usuarioLogado.getIdPadrao().equals(id)) {
            return "redirect:/erro"; // ou uma página 403 personalizada
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

        // Segurança: garante que está editando o próprio perfil
        if (!usuario.getIdPadrao().equals(userDetails.getUsuario().getIdPadrao())) {
            return "redirect:/erro";
        }

        // Atualiza os campos permitidos
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setApelido(usuarioAtualizado.getApelido());
        usuario.setEmail(usuarioAtualizado.getEmail());

        usuarioRepository.save(usuario);

        return "redirect:/usuarios/perfil";
    }

}
