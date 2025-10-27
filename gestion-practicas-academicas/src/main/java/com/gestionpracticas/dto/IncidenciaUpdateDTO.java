package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class IncidenciaUpdateDTO {
    private LocalDate fecha;
    private String tipo;
    private String descripcion;
    private String resolucion;
    private String estado;
}