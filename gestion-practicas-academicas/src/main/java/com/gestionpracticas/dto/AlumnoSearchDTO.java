package com.gestionpracticas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoSearchDTO {
    private String nombre;
    private String apellidos;
    private String dni;
    private Long cursoId;
    private Long empresaId;
    private Long tutorPracticasId;
    private Boolean activo;
    private String searchTerm;
}
