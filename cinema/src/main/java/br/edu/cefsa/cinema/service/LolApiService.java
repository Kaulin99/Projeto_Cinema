package br.edu.cefsa.cinema.service;

import br.edu.cefsa.cinema.model.LolChampion;
import br.edu.cefsa.cinema.model.ChampionDataResponse;
import br.edu.cefsa.cinema.model.SingleLolChampionResponse;
import br.edu.cefsa.cinema.model.LolSpell; // Necessário para conversão
import br.edu.cefsa.cinema.model.LolSpellImage; // Necessário para conversão
import br.edu.cefsa.cinema.model.ChampionStats; // Necessário para conversão
import br.edu.cefsa.cinema.model.ChampionImage; // Necessário para conversão
import br.edu.cefsa.cinema.model.cache.CampeaoLoLCacheado;
import br.edu.cefsa.cinema.repository.CampeaoLoLCacheadoRepository;

import com.fasterxml.jackson.core.type.TypeReference; // Para deserializar listas JSON
import com.fasterxml.jackson.databind.ObjectMapper; // Para serializar/deserializar JSON

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço para interagir com a API Riot Data Dragon (League of Legends).
 * Implementa uma estratégia de cache local: tenta buscar da API; se falhar,
 * busca do banco de dados H2. Dados frescos da API são salvos/atualizados no cache.
 */
@Service
public class LolApiService {

    private final WebClient webClient;
    private final String gameVersion = "14.10.1"; // TODO: Considerar tornar dinâmico ou configurável

    private final CampeaoLoLCacheadoRepository campeaoCacheRepository;
    private final ObjectMapper objectMapper; // Para serializar/desserializar JSON (habilidades)

    @Autowired
    public LolApiService(WebClient.Builder webClientBuilder,
                         CampeaoLoLCacheadoRepository campeaoCacheRepository,
                         ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://ddragon.leagueoflegends.com").build();
        this.campeaoCacheRepository = campeaoCacheRepository;
        this.objectMapper = objectMapper;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    /**
     * Busca a lista de todos os campeões.
     * Primeiro tenta a API externa. Se bem-sucedido, atualiza o cache local e retorna os dados da API.
     * Se a API falhar, tenta carregar do cache local.
     * Se ambos falharem, retorna um erro.
     * @return Um Mono contendo uma coleção de LolChampion.
     */
    @Transactional // Envolve a operação de salvar no cache em uma transação
    public Mono<Collection<LolChampion>> getChampions() {
        String url = String.format("/cdn/%s/data/pt_BR/champion.json", gameVersion);

        return this.webClient.get().uri(url).retrieve()
                .bodyToMono(ChampionDataResponse.class)
                .map(response -> response.getData().values()) // Extrai a coleção de campeões do DTO de resposta
                .doOnSuccess(campeoesApi -> { // Executado se a chamada à API for bem-sucedida
                    if (campeoesApi != null && !campeoesApi.isEmpty()) {
                        System.out.println("LolApiService: Sucesso ao buscar campeões da API. Atualizando cache local...");
                        // Prepara as entidades para salvar no cache
                        List<CampeaoLoLCacheado> entidadesParaSalvar = campeoesApi.stream()
                                .map(this::converterDtoParaEntidadeCache)
                                .collect(Collectors.toList());
                        // Considerar uma estratégia de limpeza de cache mais seletiva se necessário
                        // Aqui, vamos simplesmente salvar todos, o JPA/Hibernate cuidará do update se o ID já existir.
                        campeaoCacheRepository.saveAll(entidadesParaSalvar);
                        System.out.println("LolApiService: Cache local de campeões atualizado com " + entidadesParaSalvar.size() + " registros.");
                    }
                })
                .onErrorResume(ex -> { // Executado se a chamada à API falhar
                    System.err.println("LolApiService: Falha ao buscar campeões da API - " + ex.getMessage() + ". Tentando carregar do cache local...");
                    // Busca todos os campeões cacheados (assumindo que são todos de LoL nesta tabela)
                    List<CampeaoLoLCacheado> campeoesCacheados = campeaoCacheRepository.findAllByJogo("LOL"); // Garanta que este método exista
                    if (!campeoesCacheados.isEmpty()) {
                        System.out.println("LolApiService: Campeões carregados do cache local (" + campeoesCacheados.size() + " registros).");
                        List<LolChampion> dtos = campeoesCacheados.stream()
                                .map(this::converterEntidadeCacheParaDto)
                                .collect(Collectors.toList());
                        return Mono.just(dtos); // Retorna os dados do cache
                    } else {
                        System.err.println("LolApiService: Cache local de campeões vazio ou não encontrado.");
                        // Propaga o erro original da API, adicionando contexto sobre o cache
                        return Mono.error(new RuntimeException("Falha ao buscar campeões da API e o cache local está vazio.", ex));
                    }
                });
    }

    /**
     * Busca um campeão específico pelo seu ID.
     * Tenta a API primeiro. Se bem-sucedido, atualiza o cache e retorna o dado da API.
     * Se a API falhar, tenta carregar do cache local.
     * Se ambos falharem, retorna um erro.
     * @param championId O ID do campeão.
     * @return Um Mono contendo o LolChampion, ou Mono.error se não encontrado.
     */
    @Transactional
    public Mono<LolChampion> getChampionById(String championId) {
        String url = String.format("/cdn/%s/data/pt_BR/champion/%s.json", gameVersion, championId);

        return this.webClient.get().uri(url).retrieve()
                .bodyToMono(SingleLolChampionResponse.class)
                .map(response -> response.getData().get(championId)) // Extrai o campeão do DTO de resposta
                .doOnSuccess(campeaoApi -> { // Se a API retornar sucesso e o campeão não for nulo
                    if (campeaoApi != null) {
                        System.out.println("LolApiService: Sucesso ao buscar campeão '" + championId + "' da API. Atualizando cache local...");
                        CampeaoLoLCacheado entidade = converterDtoParaEntidadeCache(campeaoApi);
                        campeaoCacheRepository.save(entidade); // Salva/Atualiza o campeão específico no cache
                        System.out.println("LolApiService: Cache local para campeão '" + championId + "' atualizado.");
                    }
                })
                .onErrorResume(ex -> { // Se a chamada à API falhar
                    System.err.println("LolApiService: Falha ao buscar campeão '" + championId + "' da API - " + ex.getMessage() + ". Tentando cache local...");
                    // Busca o campeão específico do cache pelo ID e jogo
                    Optional<CampeaoLoLCacheado> campeaoCacheadoOpt = campeaoCacheRepository.findByIdAndJogo(championId, "LOL"); // Garanta que este método exista
                    if (campeaoCacheadoOpt.isPresent()) {
                        System.out.println("LolApiService: Campeão '" + championId + "' carregado do cache local.");
                        return Mono.just(converterEntidadeCacheParaDto(campeaoCacheadoOpt.get()));
                    } else {
                        System.err.println("LolApiService: Campeão '" + championId + "' não encontrado no cache local.");
                        return Mono.error(new RuntimeException("Falha ao buscar campeão '" + championId + "' da API e não encontrado no cache local.", ex));
                    }
                });
    }

    // --- Métodos Auxiliares de Conversão DTO <-> Entidade de Cache ---

    /**
     * Converte um DTO LolChampion (vindo da API) para uma entidade CampeaoLoLCacheado (para o banco).
     * @param dto O objeto LolChampion.
     * @return A entidade CampeaoLoLCacheado populada.
     */
    private CampeaoLoLCacheado converterDtoParaEntidadeCache(LolChampion dto) {
        if (dto == null) return null;
        CampeaoLoLCacheado entidade = new CampeaoLoLCacheado();
        entidade.setId(dto.getId());
        entidade.setNome(dto.getNome());
        entidade.setTitulo(dto.getTitulo());
        entidade.setBiografia(dto.getBiografia());

        if (dto.getImage() != null) {
            entidade.setImagemIconeFull(dto.getImage().getFull());
        }

        if (dto.getStats() != null) {
            entidade.setHp(dto.getStats().getHp());
            entidade.setArmor(dto.getStats().getArmor());
            entidade.setAttackDamage(dto.getStats().getAttackDamage());
            // Mapear outros stats se adicionados à entidade CampeaoLoLCacheado
        }

        // Serializa a lista de habilidades para uma String JSON
        if (dto.getSpells() != null && !dto.getSpells().isEmpty()) {
            try {
                entidade.setSpellsJson(objectMapper.writeValueAsString(dto.getSpells()));
            } catch (Exception e) {
                System.err.println("LolApiService: Erro ao serializar spells para JSON para o campeão " + dto.getId() + ": " + e.getMessage());
                entidade.setSpellsJson(null); // Ou "[]" para uma lista vazia
            }
        } else {
            entidade.setSpellsJson("[]"); // Representa uma lista vazia de spells
        }
        // entidade.setJogo("LOL"); // O campo 'jogo' na entidade CampeaoLoLCacheado já é final "LOL"
        entidade.setDataCache(LocalDateTime.now()); // Define a data do cache
        return entidade;
    }

    /**
     * Converte uma entidade CampeaoLoLCacheado (do banco) para um DTO LolChampion (para o frontend/API).
     * @param entidade O objeto CampeaoLoLCacheado.
     * @return O DTO LolChampion populado.
     */
    private LolChampion converterEntidadeCacheParaDto(CampeaoLoLCacheado entidade) {
        if (entidade == null) return null;
        LolChampion dto = new LolChampion();
        dto.setId(entidade.getId());
        dto.setNome(entidade.getNome());
        dto.setTitulo(entidade.getTitulo());
        dto.setBiografia(entidade.getBiografia());

        if (entidade.getImagemIconeFull() != null) {
            ChampionImage image = new ChampionImage();
            image.setFull(entidade.getImagemIconeFull());
            dto.setImage(image);
        }

        ChampionStats stats = new ChampionStats();
        stats.setHp(entidade.getHp());
        stats.setArmor(entidade.getArmor());
        stats.setAttackDamage(entidade.getAttackDamage());
        // Mapear outros stats se existirem
        dto.setStats(stats);

        // Deserializa a String JSON de habilidades para uma Lista<LolSpell>
        if (entidade.getSpellsJson() != null && !entidade.getSpellsJson().isEmpty() && !entidade.getSpellsJson().equals("[]")) {
            try {
                List<LolSpell> spells = objectMapper.readValue(entidade.getSpellsJson(), new TypeReference<List<LolSpell>>() {});
                dto.setSpells(spells);
            } catch (Exception e) {
                System.err.println("LolApiService: Erro ao deserializar spells do JSON para o campeão " + entidade.getId() + ": " + e.getMessage());
                dto.setSpells(Collections.emptyList()); // Retorna lista vazia em caso de erro
            }
        } else {
            dto.setSpells(Collections.emptyList());
        }
        return dto;
    }
}
