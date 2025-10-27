package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ObservacionDiariaSearchDTO {
    private Long alumnoId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Integer horasRealizadasMin;
    private Integer horasRealizadasMax;
}