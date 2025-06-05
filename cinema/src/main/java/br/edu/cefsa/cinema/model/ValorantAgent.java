package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Modelo para representar um Agente do Valorant com detalhes completos.
 * Utilizado para deserializar dados da API valorant-api.com, incluindo
 * informações básicas, função, habilidades e falas.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora propriedades desconhecidas no JSON
public class ValorantAgent {

    /**
     * Identificador UUID único do agente.
     * Mapeado do campo "uuid" no JSON.
     */
    @JsonProperty("uuid")
    private String uuid;

    /**
     * Nome de exibição do agente.
     * Mapeado do campo "displayName" no JSON.
     */
    @JsonProperty("displayName")
    private String nome;

    /**
     * Descrição textual (lore) do agente.
     * Mapeado do campo "description" no JSON.
     */
    @JsonProperty("description")
    private String descricao;

    /**
     * URL para a imagem de retrato completo (grande) do agente.
     * Mapeado do campo "fullPortrait" no JSON.
     */
    @JsonProperty("fullPortrait")
    private String fullPortrait;

    /**
     * Objeto contendo informações sobre a função (role) do agente.
     * Mapeado do campo "role" no JSON.
     */
    @JsonProperty("role")
    private AgentRole role;

    /**
     * Lista de habilidades do agente.
     * Mapeado do campo "abilities" no JSON.
     */
    @JsonProperty("abilities")
    private List<AgentAbility> abilities;

    // Getters e Setters
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getFullPortrait() { return fullPortrait; }
    public void setFullPortrait(String fullPortrait) { this.fullPortrait = fullPortrait; }

    public AgentRole getRole() { return role; }
    public void setRole(AgentRole role) { this.role = role; }

    public List<AgentAbility> getAbilities() { return abilities; }
    public void setAbilities(List<AgentAbility> abilities) { this.abilities = abilities; }
}

