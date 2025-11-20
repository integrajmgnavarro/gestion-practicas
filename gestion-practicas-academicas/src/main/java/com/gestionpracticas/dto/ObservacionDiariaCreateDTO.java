package com.gestionpracticas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ObservacionDiariaCreateDTO {

    // Se establece en el controlador a partir del usuario autenticado
    private Long alumnoId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotBlank(message = "Las actividades realizadas son obligatorias")
    private String actividades;

    @NotBlank(message = "La explicación de las actividades es obligatoria")
    private String explicaciones;

    private String observacionesAlumno; // Campo de texto libre para el alumno

    @NotNull(message = "Las horas realizadas son obligatorias")
    @Min(value = 0, message = "Las horas no pueden ser negativas")
    // Se asume un máximo de 8 horas diarias de práctica
    @Max(value = 8, message = "Las horas no pueden exceder 8 en un día de práctica")
    private Integer horasRealizadas;
}
