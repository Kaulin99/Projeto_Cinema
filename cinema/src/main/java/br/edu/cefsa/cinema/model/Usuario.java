/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.cefsa.cinema.model;

import java.io.Serializable;
import java.util.UUID;

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
@Table(name = "TB_USUARIO")
@Getter
@Setter
public class Usuario implements Serializable{
    
    public Usuario(String nome, String email, String apelido, String senha) {
        this.nome = nome;
        this.email = email;
        this.apelido = apelido;
        this.senha = senha;
    }
    
    private static final long serialVersionUID = 1L;
    
    /////////////////////////////////////////////////////////
    @Id
    @Basic(optional = false)
    @Column(name = "ID_PADRAO")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idPadrao;
    /////////////////////////////////////////////////////////  
    @Column(name = "NOME", nullable = false, length = 50)
    private String nome;
    /////////////////////////////////////////////////////////
    @Column(name = "EMAIL", nullable = false)
    private String email;
    /////////////////////////////////////////////////////////
    @Column(name = "NICKNAME", nullable = false)
    private String apelido;
    /////////////////////////////////////////////////////////
    @Column(name = "SENHA", nullable = false)
    private String senha;

   
}
