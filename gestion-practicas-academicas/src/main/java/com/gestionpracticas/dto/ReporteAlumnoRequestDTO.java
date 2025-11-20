package com.gestionpracticas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para la creación o actualización de un ReporteAlumno.
 * Solo contiene los datos de contenido y las IDs necesarias para establecer las relaciones.
 */
@Data
public class ReporteAlumnoRequestDTO {

    @NotNull(message = "El ID del alumno es obligatorio.")
    private Long alumnoId;

    @NotNull(message = "El ID del tutor de curso es obligatorio.")
    private Long tutorCursoId;

    @NotBlank(message = "El título del reporte es obligatorio.")
    private String titulo;

    @NotBlank(message = "El resumen ejecutivo es obligatorio.")
    private String resumenEjecutivo;

    @NotBlank(message = "Los aspectos destacados son obligatorios.")
    private String aspectosDestacados;

    @NotBlank(message = "Las áreas de mejora son obligatorias.")
    private String areasMejora;

    @Min(value = 0, message = "La calificación mínima es 0.")
    @Max(value = 100, message = "La calificación máxima es 100.")
    private Integer calificacion; // Opcional, pero con validaciones si se proporciona
}