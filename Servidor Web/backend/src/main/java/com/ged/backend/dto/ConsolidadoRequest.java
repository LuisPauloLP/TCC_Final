package com.ged.backend.dto;

import java.util.List;

public class ConsolidadoRequest {

    private List<DocumentoRequest> documentos;


    public ConsolidadoRequest() {
    }


    public List<DocumentoRequest> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(
            List<DocumentoRequest> documentos
    ) {
        this.documentos = documentos;
    }
}

