package br.edu.cefsa.cinema.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.edu.cefsa.cinema.model.AvaliacaoPersonagem;
import br.edu.cefsa.cinema.model.Usuario;

@Repository
public interface AvaliacaoPersonagemRepository extends JpaRepository<AvaliacaoPersonagem, UUID> {

    // Encontra uma avaliação específica de um usuário para um personagem de um jogo
    Optional<AvaliacaoPersonagem> findByPersonagemIdApiAndJogoAndUsuario(String personagemIdApi, String jogo, Usuario usuario);

    // Calcula a média de avaliações para um personagem específico de um jogo
    @Query("SELECT AVG(a.avaliacao) FROM AvaliacaoPersonagem a WHERE a.personagemIdApi = :personagemIdApi AND a.jogo = :jogo")
    Optional<Double> findAverageRatingByPersonagem(String personagemIdApi, String jogo);

    @Query("SELECT a.nomePersonagem, COUNT(a), AVG(a.avaliacao) " +
           "FROM AvaliacaoPersonagem a " +
           "WHERE a.jogo = 'VALORANT' " +
           "GROUP BY a.nomePersonagem " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> buscarPopularidadeValorant();
}