package br.edu.cefsa.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/genericos")
public class GenericoController {

    @GetMapping("/sobre")
    public String sobre() {
        return "genericos/sobre"; // Caminho dentro da pasta templates
    }
}
