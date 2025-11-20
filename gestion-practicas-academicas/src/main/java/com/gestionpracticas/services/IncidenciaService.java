package com.gestionpracticas.services;

import com.gestionpracticas.dto.IncidenciaCreateDTO;
import com.gestionpracticas.dto.IncidenciaDTO;

import java.util.List;

public interface IncidenciaService {
    /**
     * Crea una nueva incidencia a partir de los datos proporcionados.
     *
     * @param createDTO Los datos de la nueva incidencia.
     * @return El DTO de la incidencia creada.
     */
    IncidenciaDTO createIncidencia(IncidenciaCreateDTO createDTO);

    /**
     * Obtiene todas las incidencias asociadas a una lista de IDs de alumnos.
     * Utilizado para que el Tutor de Prácticas vea todas las incidencias de sus alumnos asignados.
     *
     * @param alumnoIds Lista de IDs de alumnos.
     * @return Lista de IncidenciaDTOs.
     */
    List<IncidenciaDTO> getIncidenciasByAlumnoIds(List<Long> alumnoIds);

    /**
     * Obtiene todas las incidencias asociadas a un único alumno.
     * Utilizado en la vista de detalle del alumno.
     *
     * @param alumnoId ID del alumno.
     * @return Lista de IncidenciaDTOs.
     */
    List<IncidenciaDTO> getIncidenciasByAlumnoId(Long alumnoId);
    /**
     * Obtiene una incidencia por su ID.
     * @param id ID de la incidencia.
     * @return IncidenciaDTO.
     */
    IncidenciaDTO getIncidenciaById(Long id);
    /**
     * Obtiene todas las incidencias gestionadas por un Tutor de Prácticas específico.
     * ESTE ES EL MÉTODO FALTANTE.
     * @param tutorPracticasId ID del Tutor de Prácticas.
     * @return Lista de IncidenciaDTOs.
     */
    List<IncidenciaDTO> getIncidenciasByTutorPracticasId(Long tutorPracticasId);
}
