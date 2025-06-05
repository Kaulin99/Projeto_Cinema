package br.edu.cefsa.cinema.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Import recomendado para @Param
import org.springframework.stereotype.Repository;

import br.edu.cefsa.cinema.model.AvaliacaoPersonagem;
import br.edu.cefsa.cinema.model.Usuario;

/**
 * Repositório Spring Data JPA para a entidade AvaliacaoPersonagem.
 * Fornece métodos CRUD básicos e consultas customizadas para avaliações.
 */
@Repository
public interface AvaliacaoPersonagemRepository extends JpaRepository<AvaliacaoPersonagem, UUID> {

    /**
     * Encontra uma avaliação específica feita por um usuário para um personagem de um determinado jogo.
     * Usado para verificar se o usuário já avaliou e para buscar sua avaliação atual.
     * @param personagemIdApi ID do personagem na API externa.
     * @param jogo Nome do jogo (ex: "LOL", "VALORANT").
     * @param usuario O objeto Usuario que fez a avaliação.
     * @return Um Optional contendo a AvaliacaoPersonagem se encontrada, ou Optional.empty() caso contrário.
     */
    Optional<AvaliacaoPersonagem> findByPersonagemIdApiAndJogoAndUsuario(String personagemIdApi, String jogo, Usuario usuario);

    /**
     * Calcula a média de todas as avaliações para um personagem específico de um jogo.
     * @param personagemIdApi ID do personagem na API externa.
     * @param jogo Nome do jogo.
     * @return Um Optional contendo a média (Double) se houver avaliações, ou Optional.empty() caso contrário.
     */
    @Query("SELECT AVG(a.avaliacao) FROM AvaliacaoPersonagem a WHERE a.personagemIdApi = :personagemIdApi AND a.jogo = :jogo")
    Optional<Double> findAverageRatingByPersonagem(@Param("personagemIdApi") String personagemIdApi, @Param("jogo") String jogo);

    /**
     * Busca dados agregados de popularidade para personagens de Valorant: nome, total de avaliações e média.
     * Ordena por total de avaliações (mais popular primeiro).
     * @return Lista de arrays de objetos, onde cada array contém [nomePersonagem, totalAvaliacoes, mediaAvaliacoes].
     */
    @Query("SELECT a.nomePersonagem, COUNT(a) as totalAvaliacoes, AVG(a.avaliacao) as mediaAvaliacoes " +
            "FROM AvaliacaoPersonagem a " +
            "WHERE a.jogo = 'VALORANT' " + // Filtra por jogo Valorant
            "GROUP BY a.nomePersonagem " +
            "ORDER BY totalAvaliacoes DESC, mediaAvaliacoes DESC") // Ordena por popularidade e depois por média
    List<Object[]> buscarPopularidadeValorant();

    /**
     * Busca dados agregados de popularidade para campeões de League of Legends: nome, total de avaliações e média.
     * Ordena por total de avaliações (mais popular primeiro).
     * ATENÇÃO: A query original estava filtrando por 'VALORANT'. Esta deve filtrar por 'LOL'.
     * @return Lista de arrays de objetos, onde cada array contém [nomePersonagem, totalAvaliacoes, mediaAvaliacoes].
     */
    @Query("SELECT a.nomePersonagem, COUNT(a) as totalAvaliacoes, AVG(a.avaliacao) as mediaAvaliacoes " +
            "FROM AvaliacaoPersonagem a " +
            "WHERE a.jogo = 'LOL' " + // CORREÇÃO CRÍTICA: Deve ser 'LOL' aqui
            "GROUP BY a.nomePersonagem " +
            "ORDER BY totalAvaliacoes DESC, mediaAvaliacoes DESC") // Ordena por popularidade e depois por média
    List<Object[]> buscarPopularidadeLOL();
}