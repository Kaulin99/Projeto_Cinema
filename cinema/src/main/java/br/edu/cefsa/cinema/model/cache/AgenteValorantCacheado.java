package br.edu.cefsa.cinema.model.cache;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA para armazenar dados cacheados de Agentes do Valorant.
 * Utilizada como fallback quando a API valorant-api.com estiver indisponível.
 */
@Entity
@Table(name = "tb_agente_valorant_cache")
public class AgenteValorantCacheado {

    /**
     * UUID do agente, conforme fornecido pela API (valorant-api.com).
     * Esta é a Chave Primária.
     */
    @Id
    @Column(name = "uuid_agente_api", length = 36)
    private String uuid;

    @Column(nullable = false)
    private String nome; // displayName da API

    @Lob
    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao; // Lore

    @Column(name = "url_full_portrait")
    private String fullPortraitUrl;

    // Informações da Role (simplificado para nome da role)
    @Column(name = "role_display_name")
    private String roleDisplayName;

    /**
     * Habilidades do agente, armazenadas como uma String JSON.
     */
    @Lob
    @Column(name = "abilities_json", columnDefinition = "TEXT")
    private String abilitiesJson;

    /**
     * Falas do agente (especificamente a lista de URLs dos áudios), armazenadas como String JSON.
     */
    @Lob
    @Column(name = "voice_lines_json", columnDefinition = "TEXT")
    private String voiceLinesJson;


    @Column(nullable = false, updatable = false)
    private final String jogo = "VALORANT";

    @Column(name = "data_cache", nullable = false)
    private LocalDateTime dataCache;

    // Construtor padrão
    public AgenteValorantCacheado() {
    }

    // Getters e Setters
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getFullPortraitUrl() { return fullPortraitUrl; }
    public void setFullPortraitUrl(String fullPortraitUrl) { this.fullPortraitUrl = fullPortraitUrl; }

    public String getRoleDisplayName() { return roleDisplayName; }
    public void setRoleDisplayName(String roleDisplayName) { this.roleDisplayName = roleDisplayName; }

    public String getAbilitiesJson() { return abilitiesJson; }
    public void setAbilitiesJson(String abilitiesJson) { this.abilitiesJson = abilitiesJson; }

    public String getVoiceLinesJson() { return voiceLinesJson; }
    public void setVoiceLinesJson(String voiceLinesJson) { this.voiceLinesJson = voiceLinesJson; }

    public String getJogo() { return jogo; }

    public LocalDateTime getDataCache() { return dataCache; }
    public void setDataCache(LocalDateTime dataCache) { this.dataCache = dataCache; }

    @PrePersist
    @PreUpdate
    public void onPersistOrUpdate() {
        this.dataCache = LocalDateTime.now();
    }
}