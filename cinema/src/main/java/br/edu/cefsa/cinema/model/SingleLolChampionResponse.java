package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class SingleLolChampionResponse {

    @JsonProperty("data")
    private Map<String, LolChampion> data; // A chave do mapa será o ID do campeão

    // Getter e Setter
    public Map<String, LolChampion> getData() { return data; }
    public void setData(Map<String, LolChampion> data) { this.data = data; }
}
