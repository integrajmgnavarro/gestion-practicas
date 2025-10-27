package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReporteCursoDTO {
    // Datos del curso
    private Long cursoId;
    private String cursoNombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tutorCursoNombre;
    
    // Estadísticas
    private Integer totalAlumnos;
    private Integer alumnosActivos;
    private Integer alumnosEvaluados;
    private Integer aprobados;
    private Integer suspendidos;
    private BigDecimal notaMedia;
    private BigDecimal tasaAprobados;
    
    // Distribución
    private Integer sobresalientes;
    private Integer notables;
    private Integer bienes;
    private Integer suficientes;
    private Integer insuficientes;
    
    // Empresas asociadas
    private List<String> empresasAsociadas;
    
    // Incidencias
    private Integer totalIncidencias;
    private Integer incidenciasAbiertas;
}