package br.edu.cefsa.cinema.controller;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import br.edu.cefsa.cinema.Enum.Role;
import br.edu.cefsa.cinema.model.Usuario;
import br.edu.cefsa.cinema.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/administracao")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable UUID id, Model model) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            return "redirect:/erro";
        }

        model.addAttribute("usuario", usuarioOptional.get());
        model.addAttribute("rolesDisponiveis", Arrays.asList(Role.values()));
        return "administracao/editar-usuario";
    }

    @PostMapping("/editar/{id}")
    public String salvarEdicaoUsuario(@PathVariable UUID id, @ModelAttribute Usuario usuarioAtualizado) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            return "redirect:/erro";
        }

        Usuario usuario = usuarioOptional.get();
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setApelido(usuarioAtualizado.getApelido());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setRoles(usuarioAtualizado.getRoles());

        usuarioRepository.save(usuario);
        return "redirect:/administracao/lista";
    }

    @GetMapping("/lista-usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "administracao/lista-usuarios";
    }

}