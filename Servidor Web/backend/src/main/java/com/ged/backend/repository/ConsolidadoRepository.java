package com.ged.backend.repository;

import com.ged.backend.model.Consolidado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsolidadoRepository
        extends JpaRepository<Consolidado, Long> {

    Optional<Consolidado> findByCodigoAcesso(String codigoAcesso);
}
