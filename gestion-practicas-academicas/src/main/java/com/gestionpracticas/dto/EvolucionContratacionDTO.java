package com.gestionpracticas.dto;

import lombok.Data;

@Data
public class EvolucionContratacionDTO {
    private Integer anio;
    private Integer mes; // null para agrupación anual
    private Integer contrataciones;
    private String periodo; // "2024-01" o "2024" para mejor visualización
}