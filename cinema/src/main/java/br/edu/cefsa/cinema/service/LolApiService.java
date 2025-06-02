package br.edu.cefsa.cinema.service;

import br.edu.cefsa.cinema.model.ChampionDataResponse;
import br.edu.cefsa.cinema.model.LolChampion;
import br.edu.cefsa.cinema.model.SingleLolChampionResponse; // NOVO IMPORT
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Service
public class LolApiService {

    private final WebClient webClient;
    private final String gameVersion = "14.10.1"; // Verifique a versão mais recente em https://ddragon.leagueoflegends.com/api/versions.json

    public LolApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://ddragon.leagueoflegends.com").build();
    }

    public Mono<Collection<LolChampion>> getChampions() {
        String url = String.format("/cdn/%s/data/pt_BR/champion.json", gameVersion);
        return this.webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ChampionDataResponse.class)
                .map(response -> response.getData().values());
    }

    // Em LolApiService.java
    public String getGameVersion() {
        return gameVersion;
    }

    // NOVO MÉTODO: Buscar detalhes de um campeão pelo ID
    public Mono<LolChampion> getChampionById(String championId) {
        // O ID do campeão na URL precisa ter a primeira letra maiúscula se for composto (ex: MissFortune)
        // Mas geralmente o ID que usamos (como "Aatrox", "Jinx") já funciona.
        String formattedChampionId = championId; // Ajuste se necessário para casos como "MissFortune"

        String url = String.format("/cdn/%s/data/pt_BR/champion/%s.json", gameVersion, formattedChampionId);
        return this.webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(SingleLolChampionResponse.class)
                .map(response -> response.getData().get(formattedChampionId)); // Pega o campeão de dentro do mapa "data"
    }
}