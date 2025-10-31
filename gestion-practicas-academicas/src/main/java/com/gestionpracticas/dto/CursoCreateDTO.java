package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class CursoCreateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String codigo;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @Min(value = 1, message = "La duración debe ser al menos 1 hora")
    @NotNull(message = "La duración es obligatoria")
    private Integer duracion; // en horas

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    // Relación: ID del tutor de curso
    private Long tutorCursoId;
    
    // Opcional, por defecto true en la entidad
    private Boolean activo; 
}
