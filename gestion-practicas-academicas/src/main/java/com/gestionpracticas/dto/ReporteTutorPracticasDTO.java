package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ReporteTutorPracticasDTO {
    // Datos del tutor
    private Long tutorId;
    private String tutorNombre;
    private String cargo;
    private String empresaNombre;
    
    // Alumnos
    private Integer totalAlumnos;
    private Integer alumnosActivos;
    private List<String> nombresAlumnos;
    
    // Evaluaciones realizadas
    private Integer evaluacionesRealizadas;
    private BigDecimal notaMediaOtorgada;
    
    // Evaluaciones recibidas
    private Integer evaluacionesRecibidas;
    private BigDecimal notaMediaRecibida;
    
    // Observaciones e incidencias
    private Integer observacionesRegistradas;
    private Integer incidenciasRegistradas;
}