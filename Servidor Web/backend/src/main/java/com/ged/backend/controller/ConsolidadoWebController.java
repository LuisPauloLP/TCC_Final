package com.ged.backend.controller;

import com.ged.backend.dto.ConsolidadoDetalheResponse;
import com.ged.backend.service.ConsolidadoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ConsolidadoWebController {

    private final ConsolidadoService consolidadoService;

    public ConsolidadoWebController(
            ConsolidadoService consolidadoService
    ) {
        this.consolidadoService = consolidadoService;
    }


    @GetMapping("/c/{codigo}")
    public String visualizarConsolidado(
            @PathVariable String codigo,
            Model model
    ) {

        ConsolidadoDetalheResponse consolidado =
                consolidadoService.buscarPorCodigo(codigo);

        model.addAttribute(
                "consolidado",
                consolidado
        );

        return "consolidado";
    }
}