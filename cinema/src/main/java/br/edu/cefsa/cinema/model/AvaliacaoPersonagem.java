package br.edu.cefsa.cinema.model;

import jakarta.persistence.*; // Usando jakarta.persistence para Spring Boot 3+
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA que representa a avaliação de um personagem (LoL ou Valorant) por um usuário.
 * Armazena a nota, o personagem avaliado, o jogo, e o usuário que avaliou.
 */
@Entity
@Table(name = "tb_avaliacao_personagem",
        uniqueConstraints = @UniqueConstraint(columnNames = {"personagem_id_api", "jogo", "usuario_id"}) // Garante uma avaliação por usuário/personagem/jogo
)
public class AvaliacaoPersonagem {

    /**
     * Identificador único da avaliação no banco de dados (Chave Primária).
     * Gerado automaticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * ID do personagem conforme fornecido pela API externa (ex: UUID do Valorant, ID textual do LoL).
     * Não pode ser nulo.
     */
    @Column(name = "personagem_id_api", nullable = false)
    private String personagemIdApi;

    /**
     * Nome do personagem avaliado. Armazenado para facilitar consultas e relatórios.
     * Não pode ser nulo.
     */
    @Column(name = "nome_personagem", nullable = false)
    private String nomePersonagem;

    /**
     * Indica a qual jogo o personagem pertence (ex: "VALORANT" ou "LOL").
     * Não pode ser nulo.
     */
    @Column(nullable = false)
    private String jogo;

    /**
     * Classe ou função do personagem (ex: "Duelista", "Mago").
     * Pode ser nulo se não aplicável ou não fornecido.
     */
    @Column(name = "classe_role")
    private String classeRole;

    /**
     * A nota da avaliação, geralmente de 1 a 5.
     * Não pode ser nula.
     */
    @Column(nullable = false)
    private int avaliacao;

    /**
     * Relacionamento Muitos-Para-Um com a entidade Usuario.
     * Indica qual usuário fez a avaliação. Carregado de forma LAZY para performance.
     * A coluna de junção no banco é "usuario_id" e não pode ser nula.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Data e hora em que a avaliação foi feita ou atualizada.
     * Não pode ser nula.
     */
    @Column(name = "data_avaliacao", nullable = false)
    private LocalDateTime dataAvaliacao;

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public AvaliacaoPersonagem() {
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getPersonagemIdApi() { return personagemIdApi; }
    public void setPersonagemIdApi(String personagemIdApi) { this.personagemIdApi = personagemIdApi; }

    public String getNomePersonagem() { return nomePersonagem; }
    public void setNomePersonagem(String nomePersonagem) { this.nomePersonagem = nomePersonagem; }

    public String getJogo() { return jogo; }
    public void setJogo(String jogo) { this.jogo = jogo; }

    public String getClasseRole() { return classeRole; }
    public void setClasseRole(String classeRole) { this.classeRole = classeRole; }

    public int getAvaliacao() { return avaliacao; }
    public void setAvaliacao(int avaliacao) { this.avaliacao = avaliacao; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDateTime dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }

    /**
     * Método de callback do JPA.
     * Define automaticamente a data_avaliacao para a data e hora atuais antes de persistir (salvar pela primeira vez)
     * ou atualizar a entidade.
     */
    @PrePersist
    @PreUpdate
    public void onPersistOrUpdate() {
        this.dataAvaliacao = LocalDateTime.now();
    }
}