package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class AlumnoUpdateDTO {

    @NotNull(message = "El ID del alumno es obligatorio")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI debe tener 8 números seguidos de una letra mayúscula")
    private String dni;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String telefono;

    // Relaciones (Solo IDs)
    @NotNull(message = "El curso es obligatorio")
    private Long cursoId;
    
    // Estos campos pueden ser nulos porque son opcionales en el formulario
    private Long empresaId; 
    private Long tutorPracticasId;
    
    // ID del nuevo tutor de curso
    private Long tutorCursoId; 
    
    // Datos de prácticas
    private Integer duracionPracticas;

    @Size(max = 200, message = "El horario no puede exceder 200 caracteres")
    private String horario;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    private Boolean contratado = false;
}
