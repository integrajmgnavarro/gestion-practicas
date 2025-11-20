package com.gestionpracticas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO para la actualización de una Observación Diaria.
 * Puede ser usado tanto por el Alumno (para sus campos) como por el Tutor (para sus campos).
 */
@Data
public class ObservacionDiariaUpdateDTO {

    // El ID se usa para identificar la observación a actualizar en el servicio
    @NotNull(message = "El ID de la observación es obligatorio para la actualización")
    private Long id;

    private LocalDate fecha; // El alumno podría cambiar la fecha si se equivocó

    private String actividades;

    private String explicaciones;

    private String observacionesAlumno;

    // Campo exclusivo del Tutor
    private String observacionesTutor;

    @Min(value = 0, message = "Las horas no pueden ser negativas")
    @Max(value = 8, message = "Las horas no pueden exceder 8 en un día de práctica")
    private Integer horasRealizadas;
}
