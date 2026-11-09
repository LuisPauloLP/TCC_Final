package com.ged.backend.dto;

public class ConsolidadoResponse {

    private Long id;
    private String codigoAcesso;
    private String url;


    public ConsolidadoResponse(
            Long id,
            String codigoAcesso,
            String url
    ) {
        this.id = id;
        this.codigoAcesso = codigoAcesso;
        this.url = url;
    }


    public Long getId() {
        return id;
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    public String getUrl() {
        return url;
    }
}
