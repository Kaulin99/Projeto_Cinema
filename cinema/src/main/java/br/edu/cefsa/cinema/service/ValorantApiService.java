package br.edu.cefsa.cinema.service;

import br.edu.cefsa.cinema.model.ValorantAgent;
import br.edu.cefsa.cinema.model.ValorantApiResponse;
import br.edu.cefsa.cinema.model.SingleValorantAgentResponse; // NOVO IMPORT
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ValorantApiService {
    private final WebClient webClient;

    public ValorantApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://valorant-api.com/v1").build();
    }

    // Método para buscar a lista de todos os agentes
    public Mono<List<ValorantAgent>> getAgents() {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/agents")
                        .queryParam("isPlayableCharacter", "true")
                        .queryParam("language", "pt-BR")
                        .build())
                .retrieve()
                .bodyToMono(ValorantApiResponse.class)
                .map(ValorantApiResponse::getData);
    }

    // NOVO MÉTODO: Buscar detalhes de um agente pelo UUID
    public Mono<ValorantAgent> getAgentByUuid(String uuid) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/agents/" + uuid)
                        .queryParam("language", "pt-BR")
                        .build())
                .retrieve()
                .bodyToMono(SingleValorantAgentResponse.class)
                .map(SingleValorantAgentResponse::getData); // Extrai o agente do objeto de resposta
    }
}