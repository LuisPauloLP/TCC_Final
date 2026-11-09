package com.example.ged.model;

public class Documento {

    private long id;
    private String nomeArquivo;
    private String caminhoArquivo;
    private String dataCadastro;

    private String linhaDigitavel;
    private Double valor;
    private String vencimento;
    private String beneficiario;
    private String textoOCR;

    public Documento() {
    }

    public Documento(String nomeArquivo,
                     String caminhoArquivo,
                     String dataCadastro,
                     String linhaDigitavel,
                     Double valor,
                     String vencimento,
                     String beneficiario,
                     String textoOCR) {

        this.nomeArquivo = nomeArquivo;
        this.caminhoArquivo = caminhoArquivo;
        this.dataCadastro = dataCadastro;
        this.linhaDigitavel = linhaDigitavel;
        this.valor = valor;
        this.vencimento = vencimento;
        this.beneficiario = beneficiario;
        this.textoOCR = textoOCR;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getLinhaDigitavel() {
        return linhaDigitavel;
    }

    public void setLinhaDigitavel(String linhaDigitavel) {
        this.linhaDigitavel = linhaDigitavel;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
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

    public String getTextoOCR() {
        return textoOCR;
    }

    public void setTextoOCR(String textoOCR) {
        this.textoOCR = textoOCR;
    }
}
