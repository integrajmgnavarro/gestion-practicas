package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TutorPracticasUpdateDTO {
    
    @NotNull
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

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String telefono;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 50, message = "El cargo no puede exceder 50 caracteres")
    private String cargo;

    @Size(max = 200, message = "El horario no puede exceder 200 caracteres")
    private String horario;

    // CAMPO FALTANTE: Es necesario para que el controlador pueda mapear este valor.
    private Boolean activo; 

    @NotNull(message = "Debe seleccionar una empresa")
    private Long empresaId;
}
