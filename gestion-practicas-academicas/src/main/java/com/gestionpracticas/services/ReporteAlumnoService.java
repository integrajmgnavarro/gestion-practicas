package com.gestionpracticas.services;

import com.gestionpracticas.dto.ReporteAlumnoRequestDTO;
import com.gestionpracticas.dto.ReporteAlumnoResponseDTO;
import com.gestionpracticas.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Define las operaciones de negocio para la gestión de Reportes de Alumno.
 * Estos reportes son emitidos por el Tutor de Curso al Alumno.
 */
public interface ReporteAlumnoService {

    /**
     * Emite un nuevo Reporte de Alumno, guardando el feedback del Tutor de Curso.
     * @param requestDTO Datos del reporte a crear.
     * @return El ReporteAlumnoResponseDTO del reporte emitido.
     * @throws ResourceNotFoundException Si el Alumno o el Tutor de Curso no existen.
     */
    ReporteAlumnoResponseDTO emitirReporte(ReporteAlumnoRequestDTO requestDTO) throws ResourceNotFoundException;

    /**
     * Obtiene la lista de reportes emitidos para un Alumno específico.
     * @param alumnoId El ID del Alumno.
     * @return Lista de ReporteAlumnoResponseDTO ordenados por fecha de emisión.
     */
    List<ReporteAlumnoResponseDTO> obtenerReportesPorAlumno(Long alumnoId);

    /**
     * Obtiene la lista de reportes emitidos por un Tutor de Curso específico.
     * @param tutorCursoId El ID del Tutor de Curso.
     * @return Lista de ReporteAlumnoResponseDTO ordenados por fecha de emisión.
     */
    List<ReporteAlumnoResponseDTO> obtenerReportesPorTutorCurso(Long tutorCursoId);
}