package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * DTO para representar o objeto de conteúdo completo retornado pela API de Conteúdo da Riot para Valorant.
 * Contém uma lista de "characters" entre outros tipos de conteúdo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RiotValorantContent {

    /**
     * Lista de entidades de personagens/agentes presentes no conteúdo do jogo.
     * Mapeado do campo "characters" no JSON (embora o @JsonProperty não seja estritamente necessário
     * no campo se o nome da variável for igual ao do JSON e houver getters/setters).
     */
    private List<RiotValorantCharacter> characters;

    // Getters e Setters
    public List<RiotValorantCharacter> getCharacters() { return characters; }
    public void setCharacters(List<RiotValorantCharacter> characters) { this.characters = characters; }
}