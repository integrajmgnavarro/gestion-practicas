package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoUpdateDTO {
    
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;
    
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;
    
    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String telefono;
    
    // Relaciones
    private Long cursoId;
    private Long empresaId;
    private Long tutorPracticasId;
    
    // Datos de prácticas
    @Min(value = 1, message = "La duración de prácticas debe ser al menos 1 día")
    private Integer duracionPracticas;
    
    @Size(max = 200, message = "El horario no puede exceder 200 caracteres")
    private String horario;
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    private Boolean activo;
}
