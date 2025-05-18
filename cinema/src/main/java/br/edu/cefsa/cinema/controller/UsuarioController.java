package br.edu.cefsa.cinema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.service.UsuarioService;


@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/cadastro")
    public String cadastro(@ModelAttribute Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioService.salvarUsuario(usuario);
        return "redirect:/usuarios/login";  // redireciona para a tela de login
    }


    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "redirect", required = false) String redirect,
                            Model model) {
        if (error != null) {
            model.addAttribute("mensagem", "Credenciais inválidas.");
        }
        if (logout != null) {
            model.addAttribute("mensagem", "Logout realizado com sucesso.");
        }
        if (redirect != null) {
            model.addAttribute("redirect", redirect);
        }
        return "usuarios/login";
    }
}
