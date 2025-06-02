package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List; // Importar List

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValorantAgent {

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("displayName")
    private String nome;

    @JsonProperty("description")
    private String descricao; // Esta é a Lore

    @JsonProperty("fullPortrait")
    private String fullPortrait;

    @JsonProperty("role")
    private AgentRole role;

    // Novos campos
    @JsonProperty("abilities")
    private List<AgentAbility> abilities;

    // Getters e Setters para TODOS os campos (incluindo os novos)
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

@JsonIgnoreProperties(ignoreUnknown = true)
class AgentRole {
    @JsonProperty("displayName")
    private String displayName;
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}