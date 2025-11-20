package com.gestionpracticas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorPracticasDTO {
    private Long id;
    private String nombre;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;
    
    // Atributos de la entidad TutorPracticas
    private String cargo;    // 
    private String horario; // 

    // Relación con Empresa
    private Long empresaId;
    private String empresaNombre; // Para la visualización

    // NUEVO CAMPO: Cuenta de alumnos asignados para mostrar en la tabla
    private Integer alumnosAsignados;

    // Metadatos
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}