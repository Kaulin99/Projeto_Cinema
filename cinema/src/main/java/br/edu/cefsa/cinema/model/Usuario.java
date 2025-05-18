package br.edu.cefsa.cinema.model;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import br.edu.cefsa.cinema.Enum.Role;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_usuario")
@Getter
@Setter
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    public Usuario() {}

    public Usuario(String nome, String email, String apelido, String password, Set<Role> roles) {
        this.nome = nome;
        this.email = email;
        this.apelido = apelido;
        this.password = password;
        this.roles = roles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_PADRAO")
    private UUID idPadrao;

    @Column(name = "Nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "Email", nullable = false)
    private String email;

    @Column(name = "apelido", nullable = false)
    private String apelido;

    @Column(name = "Password", nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tb_usuario_role", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;

    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}