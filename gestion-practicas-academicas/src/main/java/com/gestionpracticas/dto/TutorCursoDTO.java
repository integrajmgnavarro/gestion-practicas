package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TutorCursoDTO {
    private Long id;
    private String nombre;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;
    private String departamento;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private List<String> nombresCursos; // Lista de cursos que tutoriza
}
