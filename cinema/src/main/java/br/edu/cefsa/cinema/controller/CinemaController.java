package br.edu.cefsa.cinema.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("") // Mapeia para a raiz da aplicação e outras rotas base
public class CinemaController {

    /**
     * Mapeia a rota GET / (página inicial).
     * Adiciona informações do usuário autenticado (username e roles) ao modelo.
     * @param model Objeto para passar dados para a view.
     * @return O nome do template Thymeleaf para a página inicial (index.html).
     */
    @GetMapping("/")
    public String index(Model model) {
        // Obtém informações sobre o usuário autenticado (se houver)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("roles", auth.getAuthorities());
        } else {
            model.addAttribute("username", "Visitante"); // Ou null, dependendo de como o template trata
            model.addAttribute("roles", null);
        }
        return "index";
    }

    /**
     * Mapeia a rota GET /usuarios/cadastro para exibir o formulário de cadastro.
     * @return O nome do template Thymeleaf para a página de cadastro.
     */
    @GetMapping("/usuarios/cadastro")
    public String mostrarCadastro() {
        return "usuarios/cadastro";
    }

    /**
     * Mapeia a rota GET /usuarios/login para exibir o formulário de login.
     * @return O nome do template Thymeleaf para a página de login.
     */
    @GetMapping("/usuarios/login")
    public String mostrarLogin() {
        return "usuarios/login";
    }
}