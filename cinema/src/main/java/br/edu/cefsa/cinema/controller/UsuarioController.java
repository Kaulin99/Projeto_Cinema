package br.edu.cefsa.cinema.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

}
