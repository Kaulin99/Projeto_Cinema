package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; /**
 * Modelo para o objeto de imagem do campeão (geralmente o ícone quadrado).
 * Frequentemente definida no mesmo arquivo de LolChampion.java para conveniência.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChampionImage { // Se em arquivo separado, declarar como 'public class ChampionImage'
    /** Nome do arquivo da imagem completa (ex: "Aatrox.png"). Mapeado de "full". */
    @JsonProperty("full") private String full;

    public String getFull() { return full; }
    public void setFull(String full) { this.full = full; }
}
