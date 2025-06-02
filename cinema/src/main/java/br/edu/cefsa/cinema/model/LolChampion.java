package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List; // Importar List

@JsonIgnoreProperties(ignoreUnknown = true)
public class LolChampion {

    @JsonProperty("id") private String id;
    @JsonProperty("name") private String nome; // Usaremos este para o nome principal
    @JsonProperty("title") private String titulo;
    @JsonProperty("blurb") private String biografia; // Usaremos blurb como a lore principal
    @JsonProperty("image") private ChampionImage image; // Para o ícone quadrado
    @JsonProperty("stats") private ChampionStats stats;

    @JsonProperty("spells") // Habilidades do campeão
    private List<LolSpell> spells;

    // Getters e Setters para todos os campos
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

@JsonIgnoreProperties(ignoreUnknown = true)
class ChampionImage {
    @JsonProperty("full") private String full;

    // Getter e Setter para ChampionImage
    public String getFull() { return full; }
    public void setFull(String full) { this.full = full; }
}

// ... (código da classe LolChampion e ChampionImage permanece o mesmo) ...

@JsonIgnoreProperties(ignoreUnknown = true)
class ChampionStats { // Se esta classe estiver em seu próprio arquivo ChampionStats.java, adicione 'public' antes de 'class'
    @JsonProperty("hp")
    private double hp;

    @JsonProperty("armor")
    private double armor;

    // ----- INÍCIO DA CORREÇÃO -----
    @JsonProperty("attackdamage") // Anotação para mapear o JSON "attackdamage"
    private double attackDamage;   // Declaração do campo que estava faltando
    // ----- FIM DA CORREÇÃO -----

    // Getters e Setters para ChampionStats
    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }

    public double getArmor() {
        return armor;
    }

    public void setArmor(double armor) {
        this.armor = armor;
    }

    // Getter e Setter para attackDamage (agora com o campo declarado)
    public double getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(double attackDamage) {
        this.attackDamage = attackDamage;
    }
}
