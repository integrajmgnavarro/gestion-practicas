package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ReporteEmpresaDTO {
    // Datos de la empresa
    private Long empresaId;
    private String empresaNombre;
    private String cif;
    private String sector;
    private String personaContacto;
    
    // Tutores
    private Integer totalTutores;
    private List<String> nombresTutores;
    
    // Alumnos
    private Integer totalAlumnos;
    private Integer alumnosActivos;
    private Integer alumnosFinalizados;
    private Integer alumnosContratados;
    
    // Evaluaciones
    private Integer totalEvaluaciones;
    private BigDecimal notaMediaAlumnos;
    private BigDecimal evaluacionMediaTutores;
    
    // Incidencias
    private Integer totalIncidencias;
    private Integer incidenciasResueltas;
}