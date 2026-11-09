package com.ged.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ConsolidadoDetalheResponse {

    private Long id;
    private String codigoAcesso;
    private LocalDateTime dataCriacao;
    private List<DocumentoResponse> documentos;

    public ConsolidadoDetalheResponse(
            Long id,
            String codigoAcesso,
            LocalDateTime dataCriacao,
            List<DocumentoResponse> documentos
    ) {
        this.id = id;
        this.codigoAcesso = codigoAcesso;
        this.dataCriacao = dataCriacao;
        this.documentos = documentos;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public List<DocumentoResponse> getDocumentos() {
        return documentos;
    }
}