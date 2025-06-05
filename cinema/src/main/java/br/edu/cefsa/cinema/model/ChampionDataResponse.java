package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * DTO para representar o objeto de nível superior retornado pela API do Data Dragon (LoL),
 * especificamente do endpoint que lista todos os campeões.
 * O campo principal "data" é um mapa onde a chave é o ID do campeão e o valor são os detalhes do campeão.
 */
public class ChampionDataResponse {

    /**
     * Mapa de campeões. A chave é o ID textual do campeão (ex: "Aatrox"),
     * e o valor é um objeto LolChampion contendo os detalhes.
     * Mapeado do campo "data" no JSON.
     */
    @JsonProperty("data")
    private Map<String, LolChampion> data;

    // Getter e Setter
    public Map<String, LolChampion> getData() {
        return data;
    }

    public void setData(Map<String, LolChampion> data) {
        this.data = data;
    }
}