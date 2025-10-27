package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class IncidenciaSearchDTO {
    private Long alumnoId;
    private Long tutorPracticasId;
    private String tipo;
    private String estado;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
}