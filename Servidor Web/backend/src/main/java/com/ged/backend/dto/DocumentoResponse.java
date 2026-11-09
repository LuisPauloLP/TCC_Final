package com.ged.backend.dto;

import java.math.BigDecimal;

public class DocumentoResponse {

    private Long id;
    private String nomeArquivo;
    private String linhaDigitavel;
    private BigDecimal valor;
    private String vencimento;
    private String beneficiario;

    public DocumentoResponse(
            Long id,
            String nomeArquivo,
            String linhaDigitavel,
            BigDecimal valor,
            String vencimento,
            String beneficiario
    ) {
        this.id = id;
        this.nomeArquivo = nomeArquivo;
        this.linhaDigitavel = linhaDigitavel;
        this.valor = valor;
        this.vencimento = vencimento;
        this.beneficiario = beneficiario;
    }

    public Long getId() {
        return id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public String getLinhaDigitavel() {
        return linhaDigitavel;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getVencimento() {
        return vencimento;
    }

    public String getBeneficiario() {
        return beneficiario;
    }
}