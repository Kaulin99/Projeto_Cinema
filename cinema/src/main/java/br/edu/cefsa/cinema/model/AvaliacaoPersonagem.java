package br.edu.cefsa.cinema.model;

import jakarta.persistence.*; // Use jakarta.persistence se estiver com Spring Boot 3+
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_avaliacao_personagem",
        uniqueConstraints = @UniqueConstraint(columnNames = {"personagem_id_api", "jogo", "usuario_id"}))
public class AvaliacaoPersonagem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "personagem_id_api", nullable = false)
    private String personagemIdApi; // ID do personagem da API (UUID do Valorant, ID textual do LoL)

    @Column(name = "nome_personagem", nullable = false)
    private String nomePersonagem;

    @Column(nullable = false)
    private String jogo; // "VALORANT" ou "LOL"


    @Column(nullable = false)
    private int avaliacao; // 1 a 5

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Usuário que avaliou

    @Column(name = "data_avaliacao", nullable = false)
    private LocalDateTime dataAvaliacao;

    // Construtor padrão
    public AvaliacaoPersonagem() {
    }

    // Getters e Setters (essenciais para JPA e Thymeleaf)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPersonagemIdApi() { return personagemIdApi; }
    public void setPersonagemIdApi(String personagemIdApi) { this.personagemIdApi = personagemIdApi; }
    public String getNomePersonagem() { return nomePersonagem; }
    public void setNomePersonagem(String nomePersonagem) { this.nomePersonagem = nomePersonagem; }
    public String getJogo() { return jogo; }
    public void setJogo(String jogo) { this.jogo = jogo; }
    public int getAvaliacao() { return avaliacao; }
    public void setAvaliacao(int avaliacao) { this.avaliacao = avaliacao; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDateTime dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }

    @PrePersist // Define a data antes de persistir
    @PreUpdate  // Define a data antes de atualizar
    public void onPersistOrUpdate() {
        this.dataAvaliacao = LocalDateTime.now();
    }
}