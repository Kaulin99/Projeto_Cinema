package br.edu.cefsa.cinema.service;

import br.edu.cefsa.cinema.model.ValorantAgent;
import br.edu.cefsa.cinema.model.ValorantApiResponse;
import br.edu.cefsa.cinema.model.SingleValorantAgentResponse;
import br.edu.cefsa.cinema.model.AgentAbility; // Necessário para conversão
import br.edu.cefsa.cinema.model.AgentRole; // Necessário para conversão
import br.edu.cefsa.cinema.model.cache.AgenteValorantCacheado;
import br.edu.cefsa.cinema.repository.AgenteValorantCacheadoRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço para interagir com a API valorant-api.com.
 * Implementa uma estratégia de cache local: tenta buscar da API; se falhar,
 * busca do banco de dados H2. Dados frescos da API são salvos/atualizados no cache.
 */
@Service
public class ValorantApiService {
    private final WebClient webClient;
    private final AgenteValorantCacheadoRepository agenteCacheRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ValorantApiService(WebClient.Builder webClientBuilder,
                              AgenteValorantCacheadoRepository agenteCacheRepository,
                              ObjectMapper objectMapper) {
        final int size = 16 * 1024 * 1024; // 16MB buffer para WebClient
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        this.webClient = webClientBuilder
                .baseUrl("https://valorant-api.com/v1")
                .exchangeStrategies(strategies)
                .build();
        this.agenteCacheRepository = agenteCacheRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Busca a lista de agentes. Tenta a API primeiro, se falhar, busca do cache local.
     */
    @Transactional
    public Mono<List<ValorantAgent>> getAgents() {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/agents")
                        .queryParam("isPlayableCharacter", "true")
                        .queryParam("language", "pt-BR")
                        .build())
                .retrieve()
                .bodyToMono(ValorantApiResponse.class)
                .map(ValorantApiResponse::getData)
                .doOnSuccess(agentesApi -> {
                    if (agentesApi != null && !agentesApi.isEmpty()) {
                        System.out.println("ValorantApiService: Sucesso ao buscar agentes da API. Atualizando cache local...");
                        // Lógica para limpar cache antigo de agentes de VALORANT e salvar os novos
                        // agenteCacheRepository.deleteAllByJogo("VALORANT"); // Garanta que este método exista no repositório
                        List<AgenteValorantCacheado> paraSalvar = agentesApi.stream()
                                .map(this::converterDtoParaEntidadeCache)
                                .collect(Collectors.toList());
                        agenteCacheRepository.saveAll(paraSalvar);
                        System.out.println("ValorantApiService: Cache local de agentes atualizado com " + paraSalvar.size() + " registros.");
                    }
                })
                .onErrorResume(ex -> {
                    System.err.println("ValorantApiService: Falha ao buscar agentes da API - " + ex.getMessage() + ". Tentando cache local...");
                    List<AgenteValorantCacheado> agentesCacheados = agenteCacheRepository.findAllByJogo("VALORANT"); // Garanta que este método exista
                    if (!agentesCacheados.isEmpty()) {
                        System.out.println("ValorantApiService: Agentes carregados do cache local (" + agentesCacheados.size() + " registros).");
                        List<ValorantAgent> dtos = agentesCacheados.stream()
                                .map(this::converterEntidadeCacheParaDto)
                                .collect(Collectors.toList());
                        return Mono.just(dtos);
                    } else {
                        System.err.println("ValorantApiService: Cache local de agentes vazio.");
                        return Mono.error(new RuntimeException("Falha ao buscar agentes da API e cache local está vazio.", ex));
                    }
                });
    }

    /**
     * Busca um agente específico pelo UUID. Tenta a API primeiro, se falhar, busca do cache local.
     */
    @Transactional
    public Mono<ValorantAgent> getAgentByUuid(String uuid) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/agents/" + uuid)
                        .queryParam("language", "pt-BR")
                        .build())
                .retrieve()
                .bodyToMono(SingleValorantAgentResponse.class)
                .map(SingleValorantAgentResponse::getData)
                .doOnSuccess(agenteApi -> {
                    if (agenteApi != null) {
                        System.out.println("ValorantApiService: Sucesso ao buscar agente '" + uuid + "' da API. Atualizando cache local...");
                        AgenteValorantCacheado entidade = converterDtoParaEntidadeCache(agenteApi);
                        agenteCacheRepository.save(entidade);
                        System.out.println("ValorantApiService: Cache local para agente '" + uuid + "' atualizado.");
                    }
                })
                .onErrorResume(ex -> {
                    System.err.println("ValorantApiService: Falha ao buscar agente '" + uuid + "' da API - " + ex.getMessage() + ". Tentando cache local...");
                    Optional<AgenteValorantCacheado> agenteCacheadoOpt = agenteCacheRepository.findByUuidAndJogo(uuid, "VALORANT"); // Garanta que este método exista
                    if (agenteCacheadoOpt.isPresent()) {
                        System.out.println("ValorantApiService: Agente '" + uuid + "' carregado do cache local.");
                        return Mono.just(converterEntidadeCacheParaDto(agenteCacheadoOpt.get()));
                    } else {
                        System.err.println("ValorantApiService: Agente '" + uuid + "' não encontrado no cache local.");
                        return Mono.error(new RuntimeException("Falha ao buscar agente '" + uuid + "' da API e não encontrado no cache local.", ex));
                    }
                });
    }

    // --- Métodos Auxiliares de Conversão DTO <-> Entidade de Cache ---

    private AgenteValorantCacheado converterDtoParaEntidadeCache(ValorantAgent dto) {
        if (dto == null) return null;
        AgenteValorantCacheado entidade = new AgenteValorantCacheado();
        entidade.setUuid(dto.getUuid());
        entidade.setNome(dto.getNome());
        entidade.setDescricao(dto.getDescricao());
        entidade.setFullPortraitUrl(dto.getFullPortrait());
        if (dto.getRole() != null) {
            entidade.setRoleDisplayName(dto.getRole().getDisplayName());
        }

        try {
            if (dto.getAbilities() != null && !dto.getAbilities().isEmpty()) {
                entidade.setAbilitiesJson(objectMapper.writeValueAsString(dto.getAbilities()));
            } else {
                entidade.setAbilitiesJson("[]");
            }
        } catch (Exception e) {
            System.err.println("ValorantApiService: Erro ao serializar abilities para JSON para o agente " + dto.getUuid() + ": " + e.getMessage());
            entidade.setAbilitiesJson("[]");
            entidade.setVoiceLinesJson("[]");
        }
        // entidade.setJogo("VALORANT"); // Já é final na entidade
        entidade.setDataCache(LocalDateTime.now());
        return entidade;
    }

    private ValorantAgent converterEntidadeCacheParaDto(AgenteValorantCacheado entidade) {
        if (entidade == null) return null;
        ValorantAgent dto = new ValorantAgent();
        dto.setUuid(entidade.getUuid());
        dto.setNome(entidade.getNome());
        dto.setDescricao(entidade.getDescricao());
        dto.setFullPortrait(entidade.getFullPortraitUrl());

        if (entidade.getRoleDisplayName() != null) {
            AgentRole role = new AgentRole();
            role.setDisplayName(entidade.getRoleDisplayName());
            dto.setRole(role);
        }

        try {
            if (entidade.getAbilitiesJson() != null && !entidade.getAbilitiesJson().isEmpty() && !entidade.getAbilitiesJson().equals("[]")) {
                List<AgentAbility> abilities = objectMapper.readValue(entidade.getAbilitiesJson(), new TypeReference<List<AgentAbility>>() {});
                dto.setAbilities(abilities);
            } else {
                dto.setAbilities(Collections.emptyList());
            }


        } catch (Exception e) {
            System.err.println("ValorantApiService: Erro ao deserializar abilities/voiceLines do JSON para o agente " + entidade.getUuid() + ": " + e.getMessage());
            dto.setAbilities(Collections.emptyList());
        }
        return dto;
    }
}
