package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RiotValorantCharacter {

    @JsonProperty("uuid") // Mapeia o campo 'uuid' do JSON para 'id'
    private String id;

    @JsonProperty("displayName") // Mapeia o campo 'displayName' do JSON para 'name'
    private String name;

    // Mapeia o objeto aninhado 'localizedNames' para um Mapa
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
     * Método de conveniência para pegar o nome em Português do Brasil (pt-BR) diretamente do mapa.
     * @return O nome em pt-BR, ou o nome padrão se não for encontrado.
     */
    public String getNomeEmPortugues() {
        if (localizedNames != null && localizedNames.containsKey("pt-BR")) {
            return localizedNames.get("pt-BR");
        }
        // Se, por algum motivo, não houver nome em pt-BR, retorna o nome padrão em inglês.
        return this.name;
    }
}