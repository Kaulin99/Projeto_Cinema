package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Modelo para representar um Campeão do League of Legends com seus detalhes.
 * Utilizado para deserializar dados da API Data Dragon.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LolChampion {

    /** Identificador textual único do campeão (ex: "Aatrox"). Mapeado de "id". */
    @JsonProperty("id") private String id;
    /** Nome de exibição do campeão (ex: "Aatrox"). Mapeado de "name". */
    @JsonProperty("name") private String nome;
    /** Título do campeão (ex: "a Espada Darkin"). Mapeado de "title". */
    @JsonProperty("title") private String titulo;
    /** Biografia curta/resumo da lore do campeão. Mapeado de "blurb". */
    @JsonProperty("blurb") private String biografia;
    /** Objeto contendo informações da imagem do ícone quadrado do campeão. Mapeado de "image". */
    @JsonProperty("image") private ChampionImage image;
    /** Objeto contendo as estatísticas base do campeão. Mapeado de "stats". */
    @JsonProperty("stats") private ChampionStats stats;
    /** Lista de habilidades (spells) do campeão. Mapeado de "spells". */
    @JsonProperty("spells")
    private List<LolSpell> spells;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }

    public ChampionImage getImage() { return image; }
    public void setImage(ChampionImage image) { this.image = image; }

    public ChampionStats getStats() { return stats; }
    public void setStats(ChampionStats stats) { this.stats = stats; }

    public List<LolSpell> getSpells() { return spells; }
    public void setSpells(List<LolSpell> spells) { this.spells = spells; }
}

