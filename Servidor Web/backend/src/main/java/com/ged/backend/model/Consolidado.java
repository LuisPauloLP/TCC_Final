package com.ged.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consolidados")
public class Consolidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "codigo_acesso",
            nullable = false,
            unique = true
    )
    private String codigoAcesso;

    @Column(
            name = "data_criacao",
            nullable = false
    )
    private LocalDateTime dataCriacao;

    @OneToMany(
            mappedBy = "consolidado",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Documento> documentos = new ArrayList<>();


    public Consolidado() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    public void setCodigoAcesso(String codigoAcesso) {
        this.codigoAcesso = codigoAcesso;
    }


    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }


    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }
}