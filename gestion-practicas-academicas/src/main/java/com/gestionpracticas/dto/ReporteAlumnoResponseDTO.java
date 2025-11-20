package com.gestionpracticas.dto;

import com.gestionpracticas.models.ReporteAlumno;
import com.gestionpracticas.models.Alumno;
import com.gestionpracticas.models.TutorCurso;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO utilizado para devolver los datos de un ReporteAlumno.
 * Los nombres de los campos han sido actualizados para coincidir
 * con los getters de la entidad ReporteAlumno (resumenEjecutivo, aspectosDestacados, areasMejora).
 */
@Data
public class ReporteAlumnoResponseDTO {

    private Long id;
    private LocalDate fechaEmision;
    
    // --- NOMBRES DE CAMPOS ACTUALIZADOS SEGÚN LA ENTIDAD REPORTES ---
    private String titulo; // Agregado para mapear el título
    private String resumenEjecutivo; // Antes: resumenDesempeno
    private String aspectosDestacados; // Antes: fortalezas
    private String areasMejora; // Antes: recomendaciones (coincide con el getter de la entidad)

    // --- Información de Alumno Relacionada ---
    private Long alumnoId;
    private String nombreCompletoAlumno;
    private LocalDate fechaNacimiento;

    // --- Información de Tutor Relacionada ---
    private Long tutorCursoId;
    private String nombreCompletoTutorCurso;

    /**
     * Método estático para convertir la entidad a DTO.
     * @param entity La entidad ReporteAlumno (debe tener sus relaciones Alumno y TutorCurso cargadas).
     */
    public static ReporteAlumnoResponseDTO fromEntity(ReporteAlumno entity) {
        ReporteAlumnoResponseDTO dto = new ReporteAlumnoResponseDTO();

        // 1. Mapeo de campos directos de ReporteAlumno
        dto.setId(entity.getId());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setTitulo(entity.getTitulo()); // Mapeo de título

        // Mapeo de campos de contenido (usando los getters de la entidad)
        dto.setResumenEjecutivo(entity.getResumenEjecutivo());
        dto.setAspectosDestacados(entity.getAspectosDestacados());
        dto.setAreasMejora(entity.getAreasMejora());

        // 2. Mapeo de información de Alumno
        Alumno alumno = entity.getAlumno();
        if (alumno != null) {
            dto.setAlumnoId(alumno.getId());
            // Uso de getApellidos() (plural), lo que resuelve el error anterior
            String nombreCompleto = alumno.getNombre() + " " + alumno.getApellidos();
            dto.setNombreCompletoAlumno(nombreCompleto);
            dto.setFechaNacimiento(alumno.getFechaNacimiento());
        }

        // 3. Mapeo de información de TutorCurso (Asumiendo que TutorCurso también usa getApellidos())
        TutorCurso tutor = entity.getTutorCurso();
        if (tutor != null) {
            dto.setTutorCursoId(tutor.getId());
            // Esto resuelve el error "The method getApellido() is undefined for the type TutorCurso"
            String nombreCompleto = tutor.getNombre() + " " + tutor.getApellidos();
            dto.setNombreCompletoTutorCurso(nombreCompleto);
        }

        return dto;
    }
}
