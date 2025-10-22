package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoCreateDTO {
    
    // Datos personales
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
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;
    
    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String telefono;
    
    // Contraseña para el usuario
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    
    // Relaciones (IDs)
    @NotNull(message = "El curso es obligatorio")
    private Long cursoId;
    
    private Long empresaId; // Puede ser null al principio
    
    private Long tutorPracticasId; // Puede ser null al principio
    
    // Datos de prácticas
    @Min(value = 1, message = "La duración de prácticas debe ser al menos 1 día")
    private Integer duracionPracticas;
    
    @Size(max = 200, message = "El horario no puede exceder 200 caracteres")
    private String horario;
    
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDate fechaInicio;
    
    private LocalDate fechaFin;
}
