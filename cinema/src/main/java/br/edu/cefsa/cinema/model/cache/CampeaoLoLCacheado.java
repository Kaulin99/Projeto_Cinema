package br.edu.cefsa.cinema.model.cache;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidade JPA para armazenar dados cacheados de Campeões do League of Legends.
 * Utilizada como fallback quando a API Data Dragon estiver indisponível.
 */
@Entity
@Table(name = "tb_campeao_lol_cache")
public class CampeaoLoLCacheado {

    /**
     * ID do campeão, conforme fornecido pela API Data Dragon (ex: "Aatrox").
     * Esta é a Chave Primária para esta entidade de cache.
     */
    @Id
    @Column(name = "id_campeao_api", length = 100) // Ajuste o tamanho conforme necessário
    private String id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 255)
    private String titulo;

    @Lob // Para textos mais longos como a biografia/lore
    @Column(name = "biografia", columnDefinition = "TEXT")
    private String biografia; // Equivalente ao 'blurb'

    // Informações da imagem do ícone quadrado
    @Column(name = "imagem_icone_full")
    private String imagemIconeFull; // Ex: "Aatrox.png"

    // Estatísticas base (simplificado, você pode adicionar mais ou usar JSON)
    private double hp;
    private double armor;
    private double attackDamage;
    // Adicione outros stats se desejar...

    /**
     * Habilidades (spells) do campeão, armazenadas como uma String JSON.
     * Requer serialização/deserialização manual no serviço ao converter para/de LolChampion DTO.
     */
    @Lob
    @Column(name = "spells_json", columnDefinition = "TEXT")
    private String spellsJson;

    /**
     * Campo constante para identificar o jogo.
     * Útil se você decidir ter uma única tabela de cache para todos os personagens.
     */
    @Column(nullable = false, updatable = false)
    private final String jogo = "LOL";

    /**
     * Data e hora em que este registro de cache foi criado ou atualizado pela última vez.
     */
    @Column(name = "data_cache", nullable = false)
    private LocalDateTime dataCache;

    // Construtor padrão
    public CampeaoLoLCacheado() {
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getImagemIconeFull() {
        return imagemIconeFull;
    }

    public void setImagemIconeFull(String imagemIconeFull) {
        this.imagemIconeFull = imagemIconeFull;
    }

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

    public double getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(double attackDamage) {
        this.attackDamage = attackDamage;
    }

    public String getSpellsJson() {
        return spellsJson;
    }

    public void setSpellsJson(String spellsJson) {
        this.spellsJson = spellsJson;
    }

    public String getJogo() {
        return jogo;
    }
    // Não há setter para 'jogo' pois é final e definido no momento da criação implícita.

    public LocalDateTime getDataCache() {
        return dataCache;
    }

    public void setDataCache(LocalDateTime dataCache) {
        this.dataCache = dataCache;
    }

    @PrePersist
    @PreUpdate
    public void onPersistOrUpdate() {
        this.dataCache = LocalDateTime.now();
    }
}