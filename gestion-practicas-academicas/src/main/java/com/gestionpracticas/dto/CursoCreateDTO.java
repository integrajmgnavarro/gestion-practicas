package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CursoCreateDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @Min(value = 1, message = "La duración debe ser al menos 1 hora")
    private Integer duracion; // en horas
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    private Long tutorCursoId; // opcional en creación
}
