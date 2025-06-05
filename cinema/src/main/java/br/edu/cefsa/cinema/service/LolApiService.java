package br.edu.cefsa.cinema.service;

import br.edu.cefsa.cinema.model.ChampionDataResponse;
import br.edu.cefsa.cinema.model.LolChampion;
import br.edu.cefsa.cinema.model.SingleLolChampionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; // Usado para chamadas HTTP reativas
import reactor.core.publisher.Mono; // Tipo reativo para representar 0 ou 1 resultado

import java.util.Collection;

/**
 * Serviço para interagir com a API Riot Data Dragon para League of Legends.
 * Responsável por buscar dados de campeões.
 */
@Service
public class LolApiService {

    private final WebClient webClient; // Cliente HTTP para fazer as requisições
    private final String gameVersion = "14.10.1"; // Versão do Data Dragon a ser usada (verificar por atualizações)

    // Construtor que recebe um WebClient.Builder configurado pelo Spring
    public LolApiService(WebClient.Builder webClientBuilder) {
        // Configura a URL base para todas as chamadas desta instância do WebClient
        this.webClient = webClientBuilder.baseUrl("https://ddragon.leagueoflegends.com").build();
    }

    /**
     * Busca a lista de todos os campeões da versão atual do Data Dragon em Português do Brasil.
     * @return Um Mono contendo uma coleção de objetos LolChampion.
     */
    public Mono<Collection<LolChampion>> getChampions() {
        // Monta a URL específica para o endpoint de todos os campeões
        String url = String.format("/cdn/%s/data/pt_BR/champion.json", gameVersion);
        return this.webClient.get() // Prepara uma requisição GET
                .uri(url) // Define o caminho do recurso
                .retrieve() // Executa a requisição e obtém a resposta
                // Converte o corpo da resposta JSON para um objeto ChampionDataResponse
                .bodyToMono(ChampionDataResponse.class)
                // Mapeia o ChampionDataResponse para extrair apenas a coleção de LolChampion
                .map(response -> response.getData().values());
    }

    /**
     * Retorna a string da versão do jogo/API utilizada.
     * Útil para construir URLs de imagens de habilidades no frontend.
     * @return A string da versão do jogo.
     */
    public String getGameVersion() {
        return gameVersion;
    }

    /**
     * Busca dados detalhados de um campeão específico pelo seu ID.
     * @param championId O ID do campeão (ex: "Aatrox", "Jinx").
     * @return Um Mono contendo o objeto LolChampion com dados detalhados.
     */
    public Mono<LolChampion> getChampionById(String championId) {
        // IDs de campeões com nomes compostos (ex: MissFortune) geralmente não precisam de formatação especial aqui,
        // mas a API é case-sensitive para o nome do arquivo.
        String formattedChampionId = championId;

        // Monta a URL para o endpoint de um campeão específico
        String url = String.format("/cdn/%s/data/pt_BR/champion/%s.json", gameVersion, formattedChampionId);
        return this.webClient.get() // Prepara uma requisição GET
                .uri(url) // Define o caminho
                .retrieve() // Executa
                // Converte o corpo da resposta JSON para um objeto SingleLolChampionResponse
                .bodyToMono(SingleLolChampionResponse.class)
                // Extrai o objeto LolChampion de dentro do mapa "data" usando o ID do campeão como chave
                .map(response -> response.getData().get(formattedChampionId));
    }
}