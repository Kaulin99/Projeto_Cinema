package br.edu.cefsa.cinema.repository;

import br.edu.cefsa.cinema.model.AvaliacaoPersonagem;
import br.edu.cefsa.cinema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvaliacaoPersonagemRepository extends JpaRepository<AvaliacaoPersonagem, UUID> {

    // Encontra uma avaliação específica de um usuário para um personagem de um jogo
    Optional<AvaliacaoPersonagem> findByPersonagemIdApiAndJogoAndUsuario(String personagemIdApi, String jogo, Usuario usuario);

    // Calcula a média de avaliações para um personagem específico de um jogo
    @Query("SELECT AVG(a.avaliacao) FROM AvaliacaoPersonagem a WHERE a.personagemIdApi = :personagemIdApi AND a.jogo = :jogo")
    Optional<Double> findAverageRatingByPersonagem(String personagemIdApi, String jogo);
}