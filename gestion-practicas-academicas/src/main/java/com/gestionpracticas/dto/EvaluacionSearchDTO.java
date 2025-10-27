package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EvaluacionSearchDTO {
    private Long alumnoId;
    private Long tutorPracticasId;
    private Long capacidadId;
    private Long criterioId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private BigDecimal puntuacionMin;
    private BigDecimal puntuacionMax;
}