package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CapacidadEvaluacionDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer puntuacionMaxima;
    private Boolean activo;
    
    // Relación con Criterio
    private Long criterioId;
    private String criterioNombre;
    
    // Metadata
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}