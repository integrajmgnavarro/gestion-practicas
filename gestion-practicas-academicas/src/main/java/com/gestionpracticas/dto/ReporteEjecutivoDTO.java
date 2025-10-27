package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReporteEjecutivoDTO {
    // KPIs generales
    private Integer totalAlumnos;
    private Integer totalCursos;
    private Integer totalEmpresas;
    private Integer totalTutoresPracticas;
    
    // Evaluaciones
    private Integer totalEvaluaciones;
    private BigDecimal notaMediaGlobal;
    private BigDecimal tasaAprobadosGlobal;
    
    // Empleabilidad
    private Integer alumnosFinalizados;
    private Integer alumnosContratados;
    private BigDecimal porcentajeContratacion;
    
    // Incidencias
    private Integer totalIncidencias;
    private Integer incidenciasAbiertas;
    private Integer incidenciasResueltas;
    
    // Observaciones
    private Integer totalObservaciones;
    private BigDecimal promedioHorasDiarias;
}