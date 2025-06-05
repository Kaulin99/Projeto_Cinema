package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar a resposta da API valorant-api.com ao buscar dados de um único agente.
 * A resposta contém um campo "data" que é o objeto do agente em si.
 */
public class SingleValorantAgentResponse {

    /**
     * Objeto contendo os detalhes do agente.
     * Mapeado do campo "data" no JSON.
     */
    @JsonProperty("data")
    private ValorantAgent data;

    // Getter e Setter
    public ValorantAgent getData() { return data; }
    public void setData(ValorantAgent data) { this.data = data; }
}