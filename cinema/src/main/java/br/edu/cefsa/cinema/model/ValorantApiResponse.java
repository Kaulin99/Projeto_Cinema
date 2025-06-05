package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO (Data Transfer Object) para representar a resposta da API valorant-api.com
 * ao buscar a lista de todos os agentes. A resposta da API encapsula a lista
 * de agentes dentro de um campo "data".
 */
public class ValorantApiResponse {

    /**
     * Lista de agentes retornada pela API.
     * Mapeado do campo "data" no JSON.
     */
    @JsonProperty("data")
    private List<ValorantAgent> data;

    /**
     * Retorna a lista de agentes.
     * @return Uma lista de objetos ValorantAgent.
     */
    public List<ValorantAgent> getData() {
        return data;
    }

    /**
     * Define a lista de agentes.
     * @param data A lista de ValorantAgent a ser definida.
     */
    public void setData(List<ValorantAgent> data) {
        this.data = data;
    }
}