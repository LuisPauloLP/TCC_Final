package com.ged.backend.controller;

import com.ged.backend.dto.ConsolidadoRequest;
import com.ged.backend.dto.ConsolidadoResponse;
import com.ged.backend.service.ConsolidadoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ged.backend.dto.ConsolidadoDetalheResponse;

@RestController
@RequestMapping("/api/consolidados")
public class ConsolidadoController {

    private final ConsolidadoService consolidadoService;


    public ConsolidadoController(
            ConsolidadoService consolidadoService
    ) {
        this.consolidadoService =
                consolidadoService;
    }


    @PostMapping
    public ResponseEntity<ConsolidadoResponse> criar(
            @RequestBody ConsolidadoRequest request
    ) {

        ConsolidadoResponse response =
                consolidadoService.criar(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ConsolidadoDetalheResponse> buscar(
            @PathVariable String codigo
    ) {

        ConsolidadoDetalheResponse response =
                consolidadoService.buscarPorCodigo(
                        codigo
                );

        return ResponseEntity.ok(response);
    }
}