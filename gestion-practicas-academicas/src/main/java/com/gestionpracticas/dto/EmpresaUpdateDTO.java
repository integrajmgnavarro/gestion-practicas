package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmpresaUpdateDTO {

    @NotNull(message = "El ID es obligatorio para actualizar")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "El CIF es obligatorio")
    @Pattern(regexp = "^[A-Z][0-9]{7}[A-Z0-9]$", message = "El CIF debe tener un formato válido")
    @Size(max = 12, message = "El CIF no puede exceder 12 caracteres")
    private String cif; // Aunque se edita, debe seguir la validación de formato

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String telefono;

    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Size(max = 100, message = "La persona de contacto no puede exceder 100 caracteres")
    private String personaContacto;

    @Size(max = 50, message = "El sector no puede exceder 50 caracteres")
    private String sector;

    @NotNull(message = "El estado 'activo' es obligatorio")
    private Boolean activo;
}
