package com.ged.backend.service;

import com.ged.backend.dto.ConsolidadoRequest;
import com.ged.backend.dto.ConsolidadoResponse;
import com.ged.backend.dto.DocumentoRequest;
import com.ged.backend.model.Consolidado;
import com.ged.backend.model.Documento;
import com.ged.backend.repository.ConsolidadoRepository;
import com.ged.backend.dto.ConsolidadoDetalheResponse;
import com.ged.backend.dto.DocumentoResponse;

import java.util.List;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConsolidadoService {

    private final ConsolidadoRepository consolidadoRepository;


    public ConsolidadoService(
            ConsolidadoRepository consolidadoRepository
    ) {
        this.consolidadoRepository =
                consolidadoRepository;
    }


    public ConsolidadoResponse criar(
            ConsolidadoRequest request
    ) {

        Consolidado consolidado =
                new Consolidado();

        consolidado.setCodigoAcesso(
                gerarCodigoAcesso()
        );

        consolidado.setDataCriacao(
                LocalDateTime.now()
        );


        for (DocumentoRequest documentoRequest :
                request.getDocumentos()) {

            Documento documento =
                    new Documento();

            documento.setNomeArquivo(
                    documentoRequest.getNomeArquivo()
            );

            documento.setLinhaDigitavel(
                    documentoRequest.getLinhaDigitavel()
            );

            documento.setValor(
                    documentoRequest.getValor()
            );

            documento.setVencimento(
                    documentoRequest.getVencimento()
            );

            documento.setBeneficiario(
                    documentoRequest.getBeneficiario()
            );


            // Liga o documento ao consolidado
            documento.setConsolidado(
                    consolidado
            );


            // Adiciona o documento
            consolidado
                    .getDocumentos()
                    .add(documento);
        }


        Consolidado salvo =
                consolidadoRepository.save(
                        consolidado
                );


        String url =
                "http://192.168.1.25:8080/c/"
                        + salvo.getCodigoAcesso();


        return new ConsolidadoResponse(
                salvo.getId(),
                salvo.getCodigoAcesso(),
                url
        );
    }

    public ConsolidadoDetalheResponse buscarPorCodigo(
            String codigo
    ) {

        Consolidado consolidado =
                consolidadoRepository
                        .findByCodigoAcesso(codigo)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Consolidado não encontrado"
                                )
                        );


        List<DocumentoResponse> documentos =
                consolidado
                        .getDocumentos()
                        .stream()
                        .map(documento ->
                                new DocumentoResponse(
                                        documento.getId(),
                                        documento.getNomeArquivo(),
                                        documento.getLinhaDigitavel(),
                                        documento.getValor(),
                                        documento.getVencimento(),
                                        documento.getBeneficiario()
                                )
                        )
                        .toList();


        return new ConsolidadoDetalheResponse(
                consolidado.getId(),
                consolidado.getCodigoAcesso(),
                consolidado.getDataCriacao(),
                documentos
        );
    }


    private String gerarCodigoAcesso() {

        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
}