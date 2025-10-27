package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EvaluacionTutorSearchDTO {
    private Long tutorPracticasId;
    private Long tutorCursoId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private BigDecimal puntuacionMin;
    private BigDecimal puntuacionMax;
}