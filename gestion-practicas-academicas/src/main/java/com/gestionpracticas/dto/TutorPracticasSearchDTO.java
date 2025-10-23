package com.gestionpracticas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorPracticasSearchDTO {
    private String nombre;
    private String apellidos;
    private String dni;
    private Long empresaId;
    private String cargo;
    private Boolean activo;
}
