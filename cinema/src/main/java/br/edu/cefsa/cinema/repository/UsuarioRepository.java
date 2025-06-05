package br.edu.cefsa.cinema.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
// O @Repository não é estritamente necessário aqui, pois JpaRepository já é um bean gerenciado.
// Mas não causa problemas.

import br.edu.cefsa.cinema.model.Usuario;

/**
 * Repositório Spring Data JPA para a entidade Usuario.
 * Fornece métodos CRUD básicos e consultas customizadas para usuários.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * Encontra um usuário pelo seu apelido (nickname).
     * Usado principalmente para o processo de login e para verificar se um apelido já existe.
     * @param apelido O apelido do usuário a ser buscado.
     * @return Um Optional contendo o Usuario se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<Usuario> findByApelido(String apelido);
}