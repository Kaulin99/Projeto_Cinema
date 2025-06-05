package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo para representar uma habilidade de um Agente do Valorant.
 * Utilizado para deserializar dados da API valorant-api.com.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora propriedades desconhecidas no JSON para evitar erros de parsing
public class AgentAbility {

    /**
     * Nome de exibição da habilidade.
     * Mapeado do campo "displayName" no JSON.
     */
    @JsonProperty("displayName")
    private String displayName;

    /**
     * Descrição textual da habilidade.
     * Mapeado do campo "description" no JSON.
     */
    @JsonProperty("description")
    private String description;

    /**
     * URL para o ícone de exibição da habilidade.
     * Mapeado do campo "displayIcon" no JSON.
     */
    @JsonProperty("displayIcon")
    private String displayIcon;

    // Getters e Setters
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDisplayIcon() { return displayIcon; }
    public void setDisplayIcon(String displayIcon) { this.displayIcon = displayIcon; }
}