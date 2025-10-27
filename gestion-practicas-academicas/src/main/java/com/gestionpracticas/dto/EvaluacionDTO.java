package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EvaluacionDTO {
    private Long id;
    private BigDecimal puntuacion;
    private String observaciones;
    private LocalDate fecha;
    
    // Relaciones
    private Long alumnoId;
    private String alumnoNombre;
    
    private Long tutorPracticasId;
    private String tutorPracticasNombre;
    
    private Long capacidadId;
    private String capacidadNombre;
    private Integer puntuacionMaxima;
    
    private String criterioNombre;
    
    // Metadata
    private LocalDateTime fechaCreacion;
}