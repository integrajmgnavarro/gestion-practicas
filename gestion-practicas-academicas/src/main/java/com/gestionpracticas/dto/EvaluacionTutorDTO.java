package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EvaluacionTutorDTO {
    private Long id;
    private BigDecimal puntuacion;
    private String observaciones;
    private String aspectosPositivos;
    private String aspectosMejorar;
    private LocalDate fecha;
    
    // Relaciones
    private Long tutorPracticasId;
    private String tutorPracticasNombre;
    
    private Long tutorCursoId;
    private String tutorCursoNombre;
    
    // Metadata
    private LocalDateTime fechaCreacion;
}