/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.cefsa.edu.br.hotelaria.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "TB_USUARIO", schema = "CINEMA")

public class Usuario implements Serializable{
    
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
    private String apelida;
    /////////////////////////////////////////////////////////


   
}
