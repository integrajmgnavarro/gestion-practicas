package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AlumnoDTO {
    private Long id;
    private String nombre;
    private String apellidos;
    private String dni;
    private LocalDate fechaNacimiento;
    private String email;
    private String telefono;

    // Datos de Prácticas
    private Integer duracionPracticas;
    private String horario;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean contratado;

    // Relaciones (Nombres para visualización)
    private String cursoNombre;
    private String empresaNombre;
    private String tutorPracticasNombre;
    
    // <-- ¡NUEVO CAMPO! Nombre del Tutor de Curso
    private String tutorCursoNombre; 

    // Metadatos
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    // Método de utilidad para el DTO
    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }
}
