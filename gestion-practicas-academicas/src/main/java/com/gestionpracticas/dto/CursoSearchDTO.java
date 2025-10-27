package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CursoSearchDTO {
    private String nombre;
    private Long tutorCursoId;
    private Boolean activo;
    private LocalDate fechaInicioDesde;
    private LocalDate fechaInicioHasta;
    private Integer duracionMin;
    private Integer duracionMax;
}