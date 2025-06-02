package br.edu.cefsa.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

// Classe para representar o objeto de conteúdo completo da Riot
@JsonIgnoreProperties(ignoreUnknown = true)
public class RiotValorantContent {

    private List<RiotValorantCharacter> characters;

    // Getters e Setters
    public List<RiotValorantCharacter> getCharacters() { return characters; }
    public void setCharacters(List<RiotValorantCharacter> characters) { this.characters = characters; }
}