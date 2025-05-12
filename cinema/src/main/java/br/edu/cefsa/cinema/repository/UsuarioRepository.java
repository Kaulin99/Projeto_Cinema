package br.edu.cefsa.cinema.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.cefsa.cinema.model.Usuario;

public interface  UsuarioRepository extends JpaRepository<Usuario, UUID>{

}
