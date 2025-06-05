package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; /**
 * Modelo para as estatísticas base de um campeão.
 * Frequentemente definida no mesmo arquivo de LolChampion.java.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChampionStats { // Se em arquivo separado, declarar como 'public class ChampionStats'
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
