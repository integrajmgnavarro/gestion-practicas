package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ObservacionDiariaDTO {
    private Long id;
    private Long alumnoId;
    private String nombreAlumno; // Nombre completo del alumno para referencia
    private LocalDate fecha;
    private String actividades;
    private String explicaciones;
    private String observacionesAlumno;
    private String observacionesTutor; // Puede ser rellenado por el tutor
    private Integer horasRealizadas;
    private LocalDateTime fechaCreacion;
}
