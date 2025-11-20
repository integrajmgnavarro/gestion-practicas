package com.gestionpracticas.dto;

import com.gestionpracticas.models.Incidencia.EstadoIncidencia;
import com.gestionpracticas.models.Incidencia.TipoIncidencia;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la visualización de una Incidencia, incluyendo todos los estados y detalles.
 */
@Data
public class IncidenciaDTO implements Serializable {
    private Long id;
    private Long alumnoId;
    private Long tutorPracticasId;
    private LocalDate fecha; // Fecha de la incidencia
    private TipoIncidencia tipo;
    private String descripcion;
    private String resolucion;
    private EstadoIncidencia estado;
    private LocalDateTime fechaCreacion; // Fecha de registro en el sistema
    private LocalDateTime fechaResolucion;
}
