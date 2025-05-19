package br.edu.cefsa.cinema.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class CinemaController {

    @GetMapping("/")
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Usuário logado
        String username = auth.getName();

        // Roles do usuário
        var roles = auth.getAuthorities();

        model.addAttribute("username", username);
        model.addAttribute("roles", roles);

        return "index";  
    }

    @GetMapping("/usuarios/cadastro")
    public String mostrarCadastro() {
        return "usuarios/cadastro"; 
    }

    @GetMapping("/usuarios/login")
    public String mostrarLogin() {
        return "usuarios/login"; 
    }
}
