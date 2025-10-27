package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ObservacionDiariaDTO {
    private Long id;
    private LocalDate fecha;
    private String actividades;
    private String explicaciones;
    private String observacionesAlumno;
    private String observacionesTutor;
    private Integer horasRealizadas;
    
    // Relación con Alumno
    private Long alumnoId;
    private String alumnoNombre;
    
    // Metadata
    private LocalDateTime fechaCreacion;
}