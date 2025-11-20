package com.gestionpracticas.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO utilizado para la respuesta de la API (lectura y listado de alumnos),
 * incluyendo todos los campos de datos personales, de prácticas, y las referencias ID.
 */
@Data
public class AlumnoDTO {

    // Datos de Identificación
    private Long id;
    private String nombre;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;
    
    // Fechas
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    // Relaciones (IDs)
    private Long cursoId; 
    private Long empresaId;
    private Long tutorPracticasId;
    private Long tutorCursoId;
    
    // Nombres de Relaciones (AÑADIDOS PARA RESOLVER ERRORES)
    private String cursoNombre; 
    private String empresaNombre;

    // Datos de Prácticas
    private Integer duracionPracticas;
    private String horario;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    private Boolean contratado;

    // Estado
    private Boolean activo;
}