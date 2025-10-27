package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class IncidenciaCreateDTO {
    @NotNull(message = "El alumno es obligatorio")
    private Long alumnoId;
    
    @NotNull(message = "El tutor de prácticas es obligatorio")
    private Long tutorPracticasId;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotBlank(message = "El tipo es obligatorio")
    private String tipo; // FALTA, RETRASO, PROBLEMA_ACTITUD, OTROS
    
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    private String resolucion;
    
    @NotBlank(message = "El estado es obligatorio")
    private String estado; // ABIERTA, EN_PROCESO, RESUELTA (default: ABIERTA)
}