package br.edu.cefsa.cinema.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.repository.UsuarioRepository;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrarUsuario(@RequestBody String body) {
        try {
            // Ex: "João|joao@email.com|joaozinho|123456"
            String[] partes = body.split("\\|");
    
            if (partes.length < 4) {
                return ResponseEntity.badRequest().body("Dados incompletos");
            }
    
            Usuario usuario = new Usuario(
                partes[0], // nome
                partes[1], // email
                partes[2], // apelido
                partes[3]  // senha
            );
    
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Usuário salvo com sucesso! ID: " + usuario.getIdPadrao());
    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Erro ao cadastrar: " + e.getMessage());
        }
    }
}