package br.edu.cefsa.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class CinemaController {

    @GetMapping("/")
    public String index() {
        return "index";  
    }

    @GetMapping("/usuarios/cadastro")
    public String mostrarCadastro() {
        return "usuarios/cadastro"; // caminho dentro da pasta templates
    }
}
