package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO para el envío de criterios de búsqueda (ej. para un controlador de búsqueda avanzada).
 */
@Data
public class ObservacionDiariaSearchDTO {
    private Long alumnoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String palabraClaveActividades; // Para buscar texto dentro de las actividades
    private Boolean tieneObservacionesTutor; // Para filtrar si el tutor ya la revisó
}
