package com.gestionpracticas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoDTO {
    private Long id;
    private Long usuarioId;
    private String nombre;
    private String apellidos;
    private String dni;
    private LocalDate fechaNacimiento;
    private String email;
    private String telefono;
    
    // Datos de relaciones (solo IDs y nombres básicos)
    private Long cursoId;
    private String cursoNombre;
    
    private Long empresaId;
    private String empresaNombre;
    
    private Long tutorPracticasId;
    private String tutorPracticasNombre;
    
    private Long tutorCursoId;
    private String tutorCursoNombre;
    
    // Datos de prácticas
    private Integer duracionPracticas;
    private String horario;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean contratado;
    
    // Metadata
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
