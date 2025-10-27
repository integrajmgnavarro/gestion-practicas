package com.gestionpracticas.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class EstadisticasGeneralesDTO {
    // Tasa de aprobados por curso
    private List<EstadisticasItemDTO> tasaAprobadosPorCurso;
    
    // Notas medias por empresa
    private List<EstadisticasItemDTO> notasMediasPorEmpresa;
    
    // Notas medias por tutor
    private List<EstadisticasItemDTO> notasMediasPorTutor;
    
    // Distribución de calificaciones (key: rango, value: cantidad)
    private Map<String, Integer> distribucionCalificaciones;
    
    // Tiempo medio de prácticas
    private Integer duracionMediaDias;
    private Integer duracionMinimaDias;
    private Integer duracionMaximaDias;
}