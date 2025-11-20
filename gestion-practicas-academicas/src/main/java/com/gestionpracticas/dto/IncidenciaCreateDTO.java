package com.gestionpracticas.dto;

import com.gestionpracticas.models.Incidencia.TipoIncidencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO para la creación de una Incidencia por parte del Tutor de Prácticas.
 */
@Data
public class IncidenciaCreateDTO implements Serializable {

    @NotNull(message = "El ID del alumno es obligatorio")
    private Long alumnoId;

    @NotNull(message = "El ID del tutor de prácticas es obligatorio")
    private Long tutorPracticasId;

    @NotNull(message = "La fecha de la incidencia es obligatoria")
    private LocalDate fecha; // Fecha en que ocurrió la incidencia

    @NotNull(message = "El tipo de incidencia es obligatorio")
    private TipoIncidencia tipo;

    @NotBlank(message = "La descripción de la incidencia es obligatoria")
    private String descripcion;
}
