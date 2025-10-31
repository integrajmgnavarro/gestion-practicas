package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EvaluacionCreateDTO {
	private Long id;
    @NotNull(message = "El alumno es obligatorio")
    private Long alumnoId;
    
    @NotNull(message = "El tutor de prácticas es obligatorio")
    private Long tutorPracticasId;
    
    @NotNull(message = "La capacidad es obligatoria")
    private Long capacidadId;
    
    @NotNull(message = "La puntuación es obligatoria")
    @DecimalMin(value = "0.0", message = "La puntuación no puede ser negativa")
    private BigDecimal puntuacion;
    
    private String observaciones;
    
    private LocalDate fecha; // si null, se usa fecha actual
}