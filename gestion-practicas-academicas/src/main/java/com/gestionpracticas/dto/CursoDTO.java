package com.gestionpracticas.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CursoDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private Integer duracion; // en horas
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
    
    private Boolean activo;
    
    // Relación con TutorCurso
    private Long tutorCursoId;
    private String tutorCursoNombre; // Nombre completo del tutor para la vista
    
    // Metadata
    private LocalDateTime fechaCreacion;
}
