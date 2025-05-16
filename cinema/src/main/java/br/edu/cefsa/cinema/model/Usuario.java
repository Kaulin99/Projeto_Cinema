/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.cefsa.cinema.model;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import br.edu.cefsa.cinema.Enum.Role;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "tb_usuario")
@Getter
@Setter
public class Usuario implements Serializable{
    
    public Usuario(String nome, String email, String apelido, String Password,Set<Role> roles) {
        this.nome = nome;
        this.email = email;
        this.apelido = apelido;
        this.Password = Password;
        this.roles = roles;
    }

    private static final long serialVersionUID = 1L;
    
    /////////////////////////////////////////////////////////
    @Id
    @Basic(optional = false)
    @Column(name = "ID_PADRAO")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idPadrao;
    /////////////////////////////////////////////////////////  
    @Column(name = "Nome", nullable = false, length = 50)
    private String nome;
    /////////////////////////////////////////////////////////
    @Column(name = "Email", nullable = false)
    private String email;
    /////////////////////////////////////////////////////////
    @Column(name = "Nickname", nullable = false)
    private String apelido;
    /////////////////////////////////////////////////////////
    @Column(name = "Password", nullable = false)
    private String Password;
    /////////////////////////////////////////////////////////
    @Column(name = "Role", nullable = false)
    private Set<Role> roles;
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
     public void setPassword(String Password) {
        this.Password = Password;
    }
    public String getPassword() {
        return Password;
    }
   
}
