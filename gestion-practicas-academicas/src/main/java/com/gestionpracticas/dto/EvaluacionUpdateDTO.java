package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EvaluacionUpdateDTO {
    @DecimalMin(value = "0.0", message = "La puntuación no puede ser negativa")
    private BigDecimal puntuacion;
    
    private String observaciones;
    
    private LocalDate fecha;
}