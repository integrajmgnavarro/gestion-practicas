package com.gestionpracticas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorPracticasDTO {
    private Long id;
    private Long usuarioId;
    private String nombre;
    private String apellidos;
    private String dni;
    private LocalDate fechaNacimiento;
    private String email;
    private String telefono;
    private String cargo;
    
    //Datos Prácticas
    private String horario;
    
    // Datos de relaciones (solo IDs y nombres básicos)
    private Long empresaId;
    private String empresaNombre;
    
    // Metadata
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}