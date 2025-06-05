package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * DTO para representar a resposta da API do Data Dragon (LoL) ao buscar dados de um único campeão.
 * A resposta contém um campo "data" que é um mapa, onde a chave é o ID do campeão.
 */
public class SingleLolChampionResponse {

    /**
     * Mapa contendo os dados do campeão. A chave é o ID textual do campeão (ex: "Aatrox"),
     * e o valor é o objeto LolChampion com seus detalhes.
     * Mapeado do campo "data" no JSON.
     */
    @JsonProperty("data")
    private Map<String, LolChampion> data;

    // Getter e Setter
    public Map<String, LolChampion> getData() { return data; }
    public void setData(Map<String, LolChampion> data) { this.data = data; }
}