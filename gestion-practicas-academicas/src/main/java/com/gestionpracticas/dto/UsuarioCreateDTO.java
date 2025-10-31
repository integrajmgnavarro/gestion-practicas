package com.gestionpracticas.dto;

import com.gestionpracticas.models.Usuario;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioCreateDTO {
	private Long id;
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    @NotNull(message = "El rol es obligatorio")
    private Usuario.Rol rol;
    
    private Long referenceId; // ID de la entidad correspondiente (Alumno, TutorPracticas, etc.)
}