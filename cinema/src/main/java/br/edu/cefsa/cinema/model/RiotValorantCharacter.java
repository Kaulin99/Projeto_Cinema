package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Modelo para representar um personagem/entidade da API de Conteúdo do Valorant da Riot.
 * Esta estrutura é específica para o endpoint /val/content/v1/contents.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RiotValorantCharacter {

    /**
     * UUID do personagem/agente.
     * Mapeado do campo "uuid" no JSON.
     */
    @JsonProperty("uuid")
    private String id;

    /**
     * Nome de exibição interno/em inglês do personagem.
     * Mapeado do campo "displayName" no JSON. Pode vir nulo deste endpoint específico.
     */
    @JsonProperty("displayName")
    private String name;

    /**
     * Mapa contendo os nomes localizados do personagem em diferentes idiomas.
     * A chave é o código do idioma (ex: "pt-BR") e o valor é o nome traduzido.
     * Mapeado do objeto "localizedNames" no JSON.
     */
    @JsonProperty("localizedNames")
    private Map<String, String> localizedNames;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Map<String, String> getLocalizedNames() { return localizedNames; }
    public void setLocalizedNames(Map<String, String> localizedNames) { this.localizedNames = localizedNames; }

    /**
     * Método de conveniência para obter o nome do personagem em Português do Brasil (pt-BR).
     * Se o nome em pt-BR não estiver disponível no mapa localizedNames, tenta retornar o nome interno/padrão.
     * @return O nome em pt-BR, ou o nome interno/padrão se pt-BR não for encontrado ou localizedNames for nulo.
     */
    public String getNomeEmPortugues() {
        if (localizedNames != null && localizedNames.containsKey("pt-BR")) {
            return localizedNames.get("pt-BR");
        }
        // Fallback para o nome interno se o nome localizado em pt-BR não estiver disponível.
        return this.name;
    }
}