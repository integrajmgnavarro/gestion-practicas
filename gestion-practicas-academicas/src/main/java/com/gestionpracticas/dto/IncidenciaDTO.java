package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class IncidenciaDTO {
    private Long id;
    private LocalDate fecha;
    private String tipo; // FALTA, RETRASO, PROBLEMA_ACTITUD, OTROS
    private String descripcion;
    private String resolucion;
    private String estado; // ABIERTA, EN_PROCESO, RESUELTA
    
    // Relaciones
    private Long alumnoId;
    private String alumnoNombre;
    
    private Long tutorPracticasId;
    private String tutorPracticasNombre;
    
    // Metadata
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;
}