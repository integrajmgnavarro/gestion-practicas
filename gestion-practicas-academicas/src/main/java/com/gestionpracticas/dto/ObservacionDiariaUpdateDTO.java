package com.gestionpracticas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ObservacionDiariaUpdateDTO {
    private LocalDate fecha;
    private String actividades;
    private String explicaciones;
    private String observacionesAlumno;
    private String observacionesTutor;
    
    @Min(value = 0, message = "Las horas realizadas no pueden ser negativas")
    @Max(value = 24, message = "Las horas realizadas no pueden exceder 24")
    private Integer horasRealizadas;
}