package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo para representar uma habilidade (spell) de um Campeão do LoL.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LolSpell {

    /** ID interno da habilidade (ex: "AatroxQ"). Mapeado de "id". */
    @JsonProperty("id")
    private String id;

    /** Nome da habilidade. Mapeado de "name". */
    @JsonProperty("name")
    private String name;

    /** Descrição textual da habilidade. Mapeado de "description". */
    @JsonProperty("description")
    private String description;

    /** Objeto contendo informações da imagem da habilidade. Mapeado de "image". */
    @JsonProperty("image")
    private LolSpellImage image;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LolSpellImage getImage() { return image; }
    public void setImage(LolSpellImage image) { this.image = image; }
}