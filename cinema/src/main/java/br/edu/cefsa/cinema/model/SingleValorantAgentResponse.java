package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SingleValorantAgentResponse {

    @JsonProperty("data")
    private ValorantAgent data;

    // Getter e Setter
    public ValorantAgent getData() { return data; }
    public void setData(ValorantAgent data) { this.data = data; }
}