package br.edu.cefsa.cinema.repository;

import br.edu.cefsa.cinema.model.cache.CampeaoLoLCacheado; // Importa a entidade de cache
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade {@link CampeaoLoLCacheado}.
 * Fornece métodos para realizar operações CRUD (Create, Read, Update, Delete)
 * e consultas customizadas para os dados cacheados dos campeões de League of Legends.
 * A chave primária da entidade CampeaoLoLCacheado é o ID do campeão (String).
 */
@Repository
public interface CampeaoLoLCacheadoRepository extends JpaRepository<CampeaoLoLCacheado, String> {

    /**
     * Encontra todos os campeões cacheados para um jogo específico.
     * Como esta entidade é específica para LoL, o campo 'jogo' na entidade
     * será sempre "LOL". Este método é mais um exemplo de como você poderia
     * filtrar se tivesse uma tabela de cache mais genérica.
     *
     * @param jogo O nome do jogo (espera-se "LOL" para esta entidade).
     * @return Uma lista de {@link CampeaoLoLCacheado} para o jogo especificado.
     */
    List<CampeaoLoLCacheado> findAllByJogo(String jogo);

    /**
     * Encontra um campeão cacheado específico pelo seu ID da API e pelo nome do jogo.
     *
     * @param idApi O ID do campeão conforme fornecido pela API.
     * @param jogo O nome do jogo (espera-se "LOL").
     * @return Um {@link Optional} contendo o {@link CampeaoLoLCacheado} se encontrado,
     * caso contrário, um Optional vazio.
     */
    Optional<CampeaoLoLCacheado> findByIdAndJogo(String idApi, String jogo);

    /**
     * Deleta todos os registros de campeões cacheados para um jogo específico.
     * Útil para limpar o cache antes de uma nova sincronização completa.
     * Requer anotações @Modifying e @Transactional pois altera o estado do banco.
     *
     * @param jogo O nome do jogo (espera-se "LOL") cujos registros de cache serão deletados.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CampeaoLoLCacheado c WHERE c.jogo = :jogo")
    void deleteAllByJogo(@Param("jogo") String jogo);

    // Você pode adicionar outros métodos de consulta customizados aqui conforme a necessidade.
    // Por exemplo, buscar campeões cacheados com base em outros critérios,
    // ou verificar a data do último cache.
}
