package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Esta classe representa o objeto de nível superior retornado pela API do Data Dragon,
 * que contém um mapa de todos os campeões.
 */
public class ChampionDataResponse {

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