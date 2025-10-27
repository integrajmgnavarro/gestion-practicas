package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmpresaUpdateDTO {
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;
    
    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    private String telefono;
    
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;
    
    @Size(max = 100, message = "La persona de contacto no puede exceder 100 caracteres")
    private String personaContacto;
    
    @Size(max = 50, message = "El sector no puede exceder 50 caracteres")
    private String sector;
    
    private Boolean activo;
}