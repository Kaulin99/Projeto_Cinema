package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; /**
 * Modelo para a função (role) de um agente do Valorant.
 * Geralmente definida no mesmo arquivo de ValorantAgent.java ou como uma classe separada.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentRole { // Se em arquivo separado, declarar como 'public class AgentRole'
    /**
     * Nome de exibição da função (ex: "Duelista", "Controlador").
     * Mapeado do campo "displayName" no JSON.
     */
    @JsonProperty("displayName")
    private String displayName;

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
