package br.edu.cefsa.cinema.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.cefsa.cinema.Enum.Role;
import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.service.UsuarioService;


@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("cadastrar")
    public String cadastro(@ModelAttribute Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setRoles(Set.of(Role.LOGADO));
        usuarioService.salvarUsuario(usuario);
        return "redirect:/usuarios/login";  // redireciona para a tela de login
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        // Se quiser, pode passar o usuário logado para a view usando Spring Security
        return "usuarios/perfil"; // Certifique-se de que o arquivo perfil.html está nesta pasta
    }


}
