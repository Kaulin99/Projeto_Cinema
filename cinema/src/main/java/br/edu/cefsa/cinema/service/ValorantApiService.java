package br.edu.cefsa.cinema.service;

import br.edu.cefsa.cinema.model.ValorantAgent;
import br.edu.cefsa.cinema.model.ValorantApiResponse; // Para a lista de agentes
import br.edu.cefsa.cinema.model.SingleValorantAgentResponse; // Para um único agente
import org.springframework.beans.factory.annotation.Autowired; // Para o construtor, se não for final
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies; // Para aumentar o buffer
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
// import java.util.stream.Collectors; // Não usado nesta versão

/**
 * Serviço para interagir com a API comunitária valorant-api.com.
 * Responsável por buscar dados de agentes do Valorant.
 */
@Service
public class ValorantApiService {
    private final WebClient webClient;

    // Não precisa de API Key para valorant-api.com
    // @Value("${riot.api.key}")
    // private String apiKey;

    @Autowired // Opcional no construtor se for o único
    public ValorantApiService(WebClient.Builder webClientBuilder) {
        // Define o tamanho máximo do buffer em bytes (ex: 16MB) - Importante se alguma resposta for muito grande
        // Embora para valorant-api.com, as respostas sejam geralmente menores que o endpoint de conteúdo da Riot.
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        // Usando a URL base da valorant-api.com
        this.webClient = webClientBuilder
                .baseUrl("https://valorant-api.com/v1")
                .exchangeStrategies(strategies) // Aplica as estratégias (incluindo limite de memória)
                .build();
    }

    /**
     * Busca a lista de todos os agentes jogáveis do Valorant em Português do Brasil.
     * @return Um Mono contendo uma lista de objetos ValorantAgent.
     */
    public Mono<List<ValorantAgent>> getAgents() {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/agents")
                        .queryParam("isPlayableCharacter", "true") // Filtra apenas personagens jogáveis
                        .queryParam("language", "pt-BR") // Define o idioma
                        .build())
                .retrieve()
                .bodyToMono(ValorantApiResponse.class) // Mapeia a resposta para o DTO da lista
                .map(ValorantApiResponse::getData); // Extrai a lista de agentes do DTO
    }

    /**
     * Busca dados detalhados de um agente específico pelo seu UUID.
     * @param uuid O UUID do agente.
     * @return Um Mono contendo o objeto ValorantAgent com dados detalhados.
     */
    public Mono<ValorantAgent> getAgentByUuid(String uuid) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/agents/" + uuid) // Concatena o UUID na URL
                        .queryParam("language", "pt-BR") // Define o idioma
                        .build())
                .retrieve()
                .bodyToMono(SingleValorantAgentResponse.class) // Mapeia para o DTO de um único agente
                .map(SingleValorantAgentResponse::getData); // Extrai o objeto agente do DTO
    }
}