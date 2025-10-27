package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EvaluacionDetalleDTO {
    private String criterioNombre;
    private String capacidadNombre;
    private BigDecimal puntuacion;
    private Integer puntuacionMaxima;
    private LocalDate fecha;
    private String observaciones;
}