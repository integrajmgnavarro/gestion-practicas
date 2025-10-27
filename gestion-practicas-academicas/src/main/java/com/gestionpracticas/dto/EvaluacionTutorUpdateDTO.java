package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EvaluacionTutorUpdateDTO {
    @DecimalMin(value = "0.0", message = "La puntuación no puede ser negativa")
    @DecimalMax(value = "10.0", message = "La puntuación no puede exceder 10")
    private BigDecimal puntuacion;
    
    private String observaciones;
    private String aspectosPositivos;
    private String aspectosMejorar;
    
    private LocalDate fecha;
}