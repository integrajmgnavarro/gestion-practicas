package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReporteAlumnoDTO {
    // Datos personales
    private Long alumnoId;
    private String nombre;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;
    
    // Datos académicos
    private String cursoNombre;
    private String empresaNombre;
    private String tutorPracticasNombre;
    private String tutorCursoNombre;
    
    // Prácticas
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer duracionPracticas;
    private String horario;
    private Boolean contratado;
    
    // Evaluaciones
    private Integer totalEvaluaciones;
    private BigDecimal notaFinal;
    private String calificacion; // Sobresaliente, Notable, etc.
    private List<EvaluacionDetalleDTO> detalleEvaluaciones;
    
    // Observaciones
    private Integer totalObservaciones;
    private Integer horasTotales;
    
    // Incidencias
    private Integer totalIncidencias;
    private Integer incidenciasAbiertas;
}