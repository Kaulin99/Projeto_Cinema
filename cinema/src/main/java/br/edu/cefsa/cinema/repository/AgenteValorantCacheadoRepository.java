package br.edu.cefsa.cinema.repository;

import br.edu.cefsa.cinema.model.cache.AgenteValorantCacheado; // Importa a entidade de cache
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade {@link AgenteValorantCacheado}.
 * Fornece métodos para realizar operações CRUD (Create, Read, Update, Delete)
 * e consultas customizadas para os dados cacheados dos agentes do Valorant.
 * A chave primária da entidade AgenteValorantCacheado é o UUID do agente (String).
 */
@Repository
public interface AgenteValorantCacheadoRepository extends JpaRepository<AgenteValorantCacheado, String> {

    /**
     * Encontra todos os agentes cacheados para um jogo específico.
     * Como esta entidade é específica para Valorant, o campo 'jogo' na entidade
     * será sempre "VALORANT".
     *
     * @param jogo O nome do jogo (espera-se "VALORANT" para esta entidade).
     * @return Uma lista de {@link AgenteValorantCacheado} para o jogo especificado.
     */
    List<AgenteValorantCacheado> findAllByJogo(String jogo);

    /**
     * Encontra um agente cacheado específico pelo seu UUID da API e pelo nome do jogo.
     * (No caso desta entidade, o ID já é o UUID).
     *
     * @param uuid O UUID do agente conforme fornecido pela API.
     * @param jogo O nome do jogo (espera-se "VALORANT").
     * @return Um {@link Optional} contendo o {@link AgenteValorantCacheado} se encontrado,
     * caso contrário, um Optional vazio.
     */
    Optional<AgenteValorantCacheado> findByUuidAndJogo(String uuid, String jogo);
    // Nota: Como o 'uuid' é o @Id, JpaRepository já fornece findById(String uuid).
    // Este método customizado seria útil se o ID fosse composto ou se você quisesse
    // uma busca mais explícita que inclua o jogo, embora para uma entidade específica
    // de Valorant, o filtro por jogo pode ser implícito na lógica de serviço.
    // Por consistência com o repositório do LoL, mantive um exemplo similar.
    // Uma alternativa seria simplesmente usar findById(uuid) e garantir no serviço
    // que está lidando com o jogo correto.

    /**
     * Deleta todos os registros de agentes cacheados para um jogo específico.
     * Útil para limpar o cache antes de uma nova sincronização completa.
     * Requer anotações @Modifying e @Transactional.
     *
     * @param jogo O nome do jogo (espera-se "VALORANT") cujos registros de cache serão deletados.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM AgenteValorantCacheado a WHERE a.jogo = :jogo")
    void deleteAllByJogo(@Param("jogo") String jogo);

    // Você pode adicionar outros métodos de consulta customizados aqui.
}
