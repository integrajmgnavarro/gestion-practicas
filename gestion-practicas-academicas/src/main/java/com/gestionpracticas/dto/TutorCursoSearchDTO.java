package com.gestionpracticas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorCursoSearchDTO {
    private String nombre;
    private String apellidos;
    private String dni;
    private String especialidad;
    private Boolean activo;
}
