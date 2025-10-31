package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class CursoSearchDTO {
    private String nombre;
    // Añadir código para búsqueda
    private String codigo;
    
    private Long tutorCursoId;
    private Boolean activo;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicioDesde;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicioHasta;
    
    private Integer duracionMin;
    private Integer duracionMax;
}
