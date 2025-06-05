package br.edu.cefsa.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/genericos") // Define que todas as rotas neste controller começarão com /genericos
public class GenericoController {

    /**
     * Mapeia a rota GET /genericos/sobre para exibir a página "Sobre".
     * @return O nome do template Thymeleaf para a página "Sobre".
     */
    @GetMapping("/sobre")
    public String sobre() {
        return "genericos/sobre"; // Caminho para o template: templates/genericos/sobre.html
    }
}