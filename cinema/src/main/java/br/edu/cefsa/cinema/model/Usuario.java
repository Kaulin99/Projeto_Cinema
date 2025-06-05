package br.edu.cefsa.cinema.model;

import java.io.Serializable; // Implementa Serializable para permitir que objetos sejam convertidos em uma sequência de bytes
import java.util.Set;
import java.util.UUID;

import br.edu.cefsa.cinema.Enum.Role; // Importa o Enum Role
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
// As anotações @Getter e @Setter do Lombok foram removidas para usar getters/setters explícitos.

/**
 * Entidade JPA que representa um usuário do sistema.
 * Mapeada para a tabela "tb_usuario" no banco de dados.
 * Implementa Serializable, uma boa prática para entidades JPA.
 */
@Entity
@Table(name = "tb_usuario")
public class Usuario implements Serializable {

    /**
     * Número de versão serial para controle de versionamento da serialização.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificador único do usuário (Chave Primária).
     * Gerado automaticamente como um UUID.
     * Mapeado para a coluna "id_padrao".
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_padrao")
    private UUID idPadrao;

    /**
     * Nome completo do usuário.
     * Não pode ser nulo, tamanho máximo de 50 caracteres.
     * Mapeado para a coluna "nome".
     */
    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    /**
     * Endereço de e-mail do usuário.
     * Não pode ser nulo. Assume-se que seja único (embora não haja constraint aqui, geralmente há no banco ou na lógica de negócios).
     * Mapeado para a coluna "email".
     */
    @Column(name = "email", nullable = false) // Adicionar unique = true se o email deve ser único no banco
    private String email;

    /**
     * Apelido (nickname) do usuário, usado para login.
     * Não pode ser nulo. Assume-se que seja único.
     * Mapeado para a coluna "apelido".
     */
    @Column(name = "apelido", nullable = false, unique = true) // Adicionado unique = true para o apelido
    private String apelido;

    /**
     * Senha do usuário (armazenada como hash no banco de dados).
     * Não pode ser nula.
     * Mapeado para a coluna "senha".
     */
    @Column(name = "senha", nullable = false)
    private String senha;

    /**
     * Conjunto de papéis (roles) atribuídos ao usuário.
     * Usa uma tabela de junção "tb_usuario_role" para o relacionamento Muitos-Para-Muitos implícito.
     * As roles são armazenadas como Strings no banco (EnumType.STRING).
     * FetchType.EAGER carrega as roles juntamente com o usuário.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tb_usuario_role", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> roles;

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public Usuario() {}

    /**
     * Construtor para criar um usuário com os campos principais.
     * @param nome Nome completo do usuário.
     * @param email Email do usuário.
     * @param apelido Apelido (nickname) do usuário.
     * @param senha Senha (já codificada/hashed) do usuário.
     * @param roles Conjunto de papéis do usuário.
     */
    public Usuario(String nome, String email, String apelido, String senha, Set<Role> roles) {
        this.nome = nome;
        this.email = email;
        this.apelido = apelido;
        this.senha = senha;
        this.roles = roles;
    }

    // Getters e Setters explícitos

    /**
     * Retorna o ID padrão (UUID) do usuário. Também usado como o ID principal para Spring Security.
     * @return O UUID do usuário.
     */
    public UUID getIdPadrao() { // Nome original do getter que retorna idPadrao
        return idPadrao;
    }

    /**
     * Define o ID padrão (UUID) do usuário.
     * Geralmente, o ID é gerenciado pelo JPA e não definido manualmente após a criação.
     * @param idPadrao O UUID a ser definido.
     */
    public void setIdPadrao(UUID idPadrao) { // Corrigido nome de getIdPadrao(UUID) para setIdPadrao(UUID)
        this.idPadrao = idPadrao;
    }

    /**
     * Getter alternativo para o ID, usado pelo CustomUserDetails.
     * @return O UUID do usuário.
     */
    public UUID getId() { // Este método já existia e estava correto para obter o idPadrao
        return idPadrao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}