package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo para representar os dados da imagem de uma habilidade (spell) do LoL.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LolSpellImage {

    /** Nome do arquivo da imagem da habilidade (ex: "AatroxQ.png"). Mapeado de "full". */
    @JsonProperty("full")
    private String full;

    // Getter e Setter
    public String getFull() { return full; }
    public void setFull(String full) { this.full = full; }
}