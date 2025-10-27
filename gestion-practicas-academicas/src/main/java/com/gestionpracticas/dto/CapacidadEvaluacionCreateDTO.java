package com.gestionpracticas.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
@Data
public class CapacidadEvaluacionCreateDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    private String descripcion;
    
    @Min(value = 1, message = "La puntuación máxima debe ser al menos 1")
    private Integer puntuacionMaxima; // Default 10 en el service
    
    @NotNull(message = "El criterio es obligatorio")
    private Long criterioId;
}