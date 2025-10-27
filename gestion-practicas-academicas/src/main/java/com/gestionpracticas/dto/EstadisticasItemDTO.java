package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EstadisticasItemDTO {
    private Long id; // id del curso/empresa/tutor
    private String nombre; // nombre descriptivo
    private Integer totalAlumnos; // cantidad de alumnos
    private Integer aprobados; // alumnos aprobados
    private Integer suspendidos; // alumnos suspendidos
    private BigDecimal notaMedia; // nota media
    private BigDecimal tasaAprobados; // porcentaje de aprobados
    private Integer contratados; // para empleabilidad
}