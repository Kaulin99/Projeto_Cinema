package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LolSpellImage {

    @JsonProperty("full") // Ex: "AsheQ.png"
    private String full;

    // Getter e Setter
    public String getFull() { return full; }
    public void setFull(String full) { this.full = full; }
}