package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CursoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracion; // en horas
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
    
    // Relación con TutorCurso
    private Long tutorCursoId;
    private String tutorCursoNombre;
    
    // Metadata
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}