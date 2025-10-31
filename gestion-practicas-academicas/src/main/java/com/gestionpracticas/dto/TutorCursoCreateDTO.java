package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TutorCursoCreateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI debe tener 8 números seguidos de una letra mayúscula")
    @Size(max = 9, message = "El DNI no puede exceder 9 caracteres")
    private String dni;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String telefono;

    @NotBlank(message = "El departamento es obligatorio")
    @Size(max = 100, message = "El departamento no puede exceder 100 caracteres")
    private String departamento;
}
