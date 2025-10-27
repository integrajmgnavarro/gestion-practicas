package com.gestionpracticas.dto;

import lombok.Data;

@Data
public class CapacidadEvaluacionSearchDTO {
    private String nombre;
    private Long criterioId;
    private Boolean activo;
    private Integer puntuacionMaximaMin;
    private Integer puntuacionMaximaMax;
}