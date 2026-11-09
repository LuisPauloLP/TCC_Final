package com.ged.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    @Column(name = "linha_digitavel")
    private String linhaDigitavel;

    @Column(
            precision = 12,
            scale = 2
    )
    private BigDecimal valor;

    private String vencimento;

    private String beneficiario;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "consolidado_id",
            nullable = false
    )
    private Consolidado consolidado;


    public Documento() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }


    public String getLinhaDigitavel() {
        return linhaDigitavel;
    }

    public void setLinhaDigitavel(String linhaDigitavel) {
        this.linhaDigitavel = linhaDigitavel;
    }


    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }


    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }


    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }


    public Consolidado getConsolidado() {
        return consolidado;
    }

    public void setConsolidado(
            Consolidado consolidado
    ) {
        this.consolidado = consolidado;
    }
}
