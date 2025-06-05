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

/**
 * Modelo para o objeto de imagem do campeão (geralmente o ícone quadrado).
 * Frequentemente definida no mesmo arquivo de LolChampion.java para conveniência.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ChampionImage { // Se em arquivo separado, declarar como 'public class ChampionImage'
    /** Nome do arquivo da imagem completa (ex: "Aatrox.png"). Mapeado de "full". */
    @JsonProperty("full") private String full;

    public String getFull() { return full; }
    public void setFull(String full) { this.full = full; }
}

/**
 * Modelo para as estatísticas base de um campeão.
 * Frequentemente definida no mesmo arquivo de LolChampion.java.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ChampionStats { // Se em arquivo separado, declarar como 'public class ChampionStats'
    /** Pontos de vida base. Mapeado de "hp". */
    @JsonProperty("hp")
    private double hp;

    /** Armadura base. Mapeado de "armor". */
    @JsonProperty("armor")
    private double armor;

    /** Dano de ataque base. Mapeado de "attackdamage". */
    @JsonProperty("attackdamage")
    private double attackDamage;

    // Getters e Setters
    public double getHp() { return hp; }
    public void setHp(double hp) { this.hp = hp; }

    public double getArmor() { return armor; }
    public void setArmor(double armor) { this.armor = armor; }

    public double getAttackDamage() { return attackDamage; }
    public void setAttackDamage(double attackDamage) { this.attackDamage = attackDamage; }
}