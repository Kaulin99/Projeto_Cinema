package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ValorantApiResponse {

    @JsonProperty("data")
    private List<ValorantAgent> data;

    // Getter e Setter
    public List<ValorantAgent> getData() { return data; }
    public void setData(List<ValorantAgent> data) { this.data = data; }
}