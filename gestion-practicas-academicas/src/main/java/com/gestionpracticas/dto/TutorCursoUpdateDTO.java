package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorCursoUpdateDTO {
	
	@Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
	private String nombre;
    
	@Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
	private String apellidos;
	
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI debe tener 8 números seguidos de una letra mayúscula")
    private String dni;
	
	@Email(message = "El email debe ser válido")
    private String email;
    
    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String telefono;
    
    @Size(max = 100, message = "La especialidad no puede exceder 100 caracteres")
    private String especialidad;
    
    private Boolean activo;
}
