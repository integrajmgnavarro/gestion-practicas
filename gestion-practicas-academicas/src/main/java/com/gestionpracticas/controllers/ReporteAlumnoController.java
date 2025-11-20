package com.gestionpracticas.controllers;

import com.gestionpracticas.dto.ReporteAlumnoRequestDTO;
import com.gestionpracticas.dto.ReporteAlumnoResponseDTO;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.services.ReporteAlumnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Reportes de Alumnos.
 * Mapea las solicitudes HTTP a las operaciones del servicio.
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteAlumnoController {

    private final ReporteAlumnoService reporteAlumnoService;

    /**
     * Endpoint para que un Tutor de Curso emita un nuevo reporte.
     * POST /api/reportes
     * @param requestDTO Datos del reporte a crear.
     * @return El reporte creado con estado 201 (Created).
     */
    @PostMapping
    public ResponseEntity<ReporteAlumnoResponseDTO> emitirReporte(
            @Valid @RequestBody ReporteAlumnoRequestDTO requestDTO) {
        try {
            ReporteAlumnoResponseDTO response = reporteAlumnoService.emitirReporte(requestDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            // Manejo básico de error: 404 si el alumno o tutor no existe
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para obtener todos los reportes de un alumno específico.
     * GET /api/reportes/alumno/{alumnoId}
     * @param alumnoId ID del alumno.
     * @return Lista de reportes del alumno.
     */
    @GetMapping("/alumno/{alumnoId}")
    public ResponseEntity<List<ReporteAlumnoResponseDTO>> obtenerReportesPorAlumno(@PathVariable Long alumnoId) {
        List<ReporteAlumnoResponseDTO> reportes = reporteAlumnoService.obtenerReportesPorAlumno(alumnoId);
        if (reportes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content si no hay reportes
        }
        return new ResponseEntity<>(reportes, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener todos los reportes emitidos por un tutor de curso.
     * GET /api/reportes/tutor/{tutorCursoId}
     * @param tutorCursoId ID del tutor de curso.
     * @return Lista de reportes emitidos por el tutor.
     */
    @GetMapping("/tutor/{tutorCursoId}")
    public ResponseEntity<List<ReporteAlumnoResponseDTO>> obtenerReportesPorTutorCurso(@PathVariable Long tutorCursoId) {
        List<ReporteAlumnoResponseDTO> reportes = reporteAlumnoService.obtenerReportesPorTutorCurso(tutorCursoId);
        if (reportes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content si no hay reportes
        }
        return new ResponseEntity<>(reportes, HttpStatus.OK);
    }
}
