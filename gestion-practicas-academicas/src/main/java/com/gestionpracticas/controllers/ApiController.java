package com.gestionpracticas.controllers;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ApiController - Endpoints REST para operaciones AJAX
 * Todos los endpoints devuelven JSON
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final AlumnoService alumnoService;
    private final TutorService tutorService;
    private final EvaluacionService evaluacionService;
    private final EstadisticasService estadisticasService;
    private final ReportsService reportsService;

    // ========================= ALUMNOS ========================= //

    /**
     * GET /api/alumnos
     * Lista todos los alumnos
     */
    @GetMapping("/alumnos")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<List<AlumnoDTO>> getAllAlumnos() {
        List<AlumnoDTO> alumnos = alumnoService.getAllAlumnos();
        return ResponseEntity.ok(alumnos);
    }

    /**
     * GET /api/alumnos/{id}
     * Obtiene un alumno por ID
     */
    @GetMapping("/alumnos/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AlumnoDTO> getAlumnoById(@PathVariable Long id) {
        AlumnoDTO alumno = alumnoService.getAlumnoById(id);
        return ResponseEntity.ok(alumno);
    }

    /**
     * GET /api/alumnos/buscar
     * Búsqueda de alumnos con criterios
     */
    @GetMapping("/alumnos/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<List<AlumnoDTO>> searchAlumnos(@ModelAttribute AlumnoSearchDTO searchDTO) {
        List<AlumnoDTO> alumnos = alumnoService.searchAlumnos(searchDTO);
        return ResponseEntity.ok(alumnos);
    }

    /**
     * POST /api/alumnos
     * Crea un nuevo alumno
     */
    @PostMapping("/alumnos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlumnoDTO> createAlumno(@Valid @RequestBody AlumnoCreateDTO createDTO) {
        AlumnoDTO alumno = alumnoService.createAlumno(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(alumno);
    }

    /**
     * PUT /api/alumnos/{id}
     * Actualiza un alumno
     */
    @PutMapping("/alumnos/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO')")
    public ResponseEntity<AlumnoDTO> updateAlumno(@PathVariable Long id,
            @Valid @RequestBody AlumnoUpdateDTO updateDTO) {
		
			// 1. Asignar el ID de la ruta al DTO
			updateDTO.setId(id); 
			
			// 2. Llamar al servicio con la nueva firma de un solo argumento
			AlumnoDTO alumno = alumnoService.updateAlumno(updateDTO); 
			
			return ResponseEntity.ok(alumno);
    }

    /**
     * DELETE /api/alumnos/{id}
     * Elimina un alumno
     */
    @DeleteMapping("/alumnos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAlumno(@PathVariable Long id) {
        alumnoService.deleteAlumno(id);
        return ResponseEntity.noContent().build();
    }

    // ========================= TUTORES ========================= //

    /**
     * GET /api/tutores-curso
     * Lista todos los tutores de curso
     */
    @GetMapping("/tutores-curso")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TutorCursoDTO>> getAllTutoresCurso() {
        List<TutorCursoDTO> tutores = tutorService.getAllTutoresCurso();
        return ResponseEntity.ok(tutores);
    }

    /**
     * GET /api/tutores-practicas
     * Lista todos los tutores de prácticas
     */
    @GetMapping("/tutores-practicas")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<List<TutorPracticasDTO>> getAllTutoresPracticas() {
        List<TutorPracticasDTO> tutores = tutorService.getAllTutoresPracticas();
        return ResponseEntity.ok(tutores);
    }

    /**
     * POST /api/tutores-curso
     * Crea un tutor de curso
     */
    @PostMapping("/tutores-curso")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TutorCursoDTO> createTutorCurso(@Valid @RequestBody TutorCursoCreateDTO createDTO) {
        TutorCursoDTO tutor = tutorService.createTutorCurso(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(tutor);
    }

    /**
     * POST /api/tutores-practicas
     * Crea un tutor de prácticas
     */
    @PostMapping("/tutores-practicas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TutorPracticasDTO> createTutorPracticas(@Valid @RequestBody TutorPracticasCreateDTO createDTO) {
        TutorPracticasDTO tutor = tutorService.createTutorPracticas(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(tutor);
    }

    // ========================= EVALUACIONES ========================= //

    /**
     * GET /api/evaluaciones/alumno/{alumnoId}
     * Obtiene evaluaciones de un alumno
     */
    @GetMapping("/evaluaciones/alumno/{alumnoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EvaluacionDTO>> getEvaluacionesByAlumno(@PathVariable Long alumnoId) {
        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByAlumnoId(alumnoId);
        return ResponseEntity.ok(evaluaciones);
    }

    /**
     * GET /api/evaluaciones/tutor-practicas/{tutorId}
     * Obtiene evaluaciones realizadas por un tutor de prácticas
     */
    @GetMapping("/evaluaciones/tutor-practicas/{tutorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<List<EvaluacionDTO>> getEvaluacionesByTutorPracticas(@PathVariable Long tutorId) {
        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByTutorPracticasId(tutorId);
        return ResponseEntity.ok(evaluaciones);
    }

    /**
     * POST /api/evaluaciones
     * Crea una evaluación
     */
    @PostMapping("/evaluaciones")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_PRACTICAS')")
    public ResponseEntity<EvaluacionDTO> createEvaluacion(@Valid @RequestBody EvaluacionCreateDTO createDTO) {
        EvaluacionDTO evaluacion = evaluacionService.createEvaluacion(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluacion);
    }

    /**
     * PUT /api/evaluaciones/{id}
     * Actualiza una evaluación
     */
    @PutMapping("/evaluaciones/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_PRACTICAS')")
    public ResponseEntity<EvaluacionDTO> updateEvaluacion(@PathVariable Long id,
                                                          @Valid @RequestBody EvaluacionUpdateDTO updateDTO) {
        EvaluacionDTO evaluacion = evaluacionService.updateEvaluacion(id, updateDTO);
        return ResponseEntity.ok(evaluacion);
    }

    /**
     * DELETE /api/evaluaciones/{id}
     * Elimina una evaluación
     */
    @DeleteMapping("/evaluaciones/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvaluacion(@PathVariable Long id) {
        evaluacionService.deleteEvaluacion(id);
        return ResponseEntity.noContent().build();
    }

    // ========================= EVALUACIONES DE TUTORES ========================= //

    /**
     * GET /api/evaluaciones-tutores/tutor-practicas/{tutorId}
     * Obtiene evaluaciones recibidas por un tutor de prácticas
     */
    @GetMapping("/evaluaciones-tutores/tutor-practicas/{tutorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<List<EvaluacionTutorDTO>> getEvaluacionesTutorByTutorPracticas(@PathVariable Long tutorId) {
        List<EvaluacionTutorDTO> evaluaciones = evaluacionService.getEvaluacionesTutorByTutorPracticasId(tutorId);
        return ResponseEntity.ok(evaluaciones);
    }

    /**
     * POST /api/evaluaciones-tutores
     * Crea una evaluación de tutor
     */
    @PostMapping("/evaluaciones-tutores")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<EvaluacionTutorDTO> createEvaluacionTutor(@Valid @RequestBody EvaluacionTutorCreateDTO createDTO) {
        EvaluacionTutorDTO evaluacion = evaluacionService.createEvaluacionTutor(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluacion);
    }

    // ========================= CRITERIOS Y CAPACIDADES ========================= //

    /**
     * GET /api/criterios-evaluacion
     * Lista todos los criterios de evaluación
     */
    @GetMapping("/criterios-evaluacion")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CriterioEvaluacionDTO>> getAllCriteriosEvaluacion() {
        List<CriterioEvaluacionDTO> criterios = evaluacionService.getAllCriteriosEvaluacion();
        return ResponseEntity.ok(criterios);
    }

    /**
     * GET /api/capacidades-evaluacion
     * Lista todas las capacidades de evaluación
     */
    @GetMapping("/capacidades-evaluacion")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CapacidadEvaluacionDTO>> getAllCapacidadesEvaluacion() {
        List<CapacidadEvaluacionDTO> capacidades = evaluacionService.getAllCapacidadesEvaluacion();
        return ResponseEntity.ok(capacidades);
    }

    /**
     * GET /api/capacidades-evaluacion/criterio/{criterioId}
     * Obtiene capacidades por criterio
     */
    @GetMapping("/capacidades-evaluacion/criterio/{criterioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CapacidadEvaluacionDTO>> getCapacidadesByCriterio(@PathVariable Long criterioId) {
        List<CapacidadEvaluacionDTO> capacidades = evaluacionService.getCapacidadesByCriterioId(criterioId);
        return ResponseEntity.ok(capacidades);
    }

    /**
     * POST /api/criterios-evaluacion
     * Crea un criterio de evaluación
     */
    @PostMapping("/criterios-evaluacion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CriterioEvaluacionDTO> createCriterioEvaluacion(@Valid @RequestBody CriterioEvaluacionCreateDTO createDTO) {
        CriterioEvaluacionDTO criterio = evaluacionService.createCriterioEvaluacion(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(criterio);
    }

    /**
     * POST /api/capacidades-evaluacion
     * Crea una capacidad de evaluación
     */
    @PostMapping("/capacidades-evaluacion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CapacidadEvaluacionDTO> createCapacidadEvaluacion(@Valid @RequestBody CapacidadEvaluacionCreateDTO createDTO) {
        CapacidadEvaluacionDTO capacidad = evaluacionService.createCapacidadEvaluacion(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(capacidad);
    }

    // ========================= ESTADÍSTICAS ========================= //

    /**
     * GET /api/estadisticas/generales
     * Obtiene estadísticas generales
     */
    @GetMapping("/estadisticas/generales")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<EstadisticasGeneralesDTO> getEstadisticasGenerales() {
        EstadisticasGeneralesDTO estadisticas = estadisticasService.getEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * GET /api/estadisticas/empleabilidad
     * Obtiene estadísticas de empleabilidad
     */
    @GetMapping("/estadisticas/empleabilidad")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<EstadisticasEmpleabilidadDTO> getEstadisticasEmpleabilidad() {
        EstadisticasEmpleabilidadDTO estadisticas = estadisticasService.getEstadisticasEmpleabilidad();
        return ResponseEntity.ok(estadisticas);
    }

    // ========================= REPORTES ========================= //

    /**
     * GET /api/reportes/curso/{id}
     * Obtiene reporte de un curso
     */
    @GetMapping("/reportes/curso/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<ReporteCursoDTO> getReporteCurso(@PathVariable Long id) {
        ReporteCursoDTO reporte = reportsService.getReporteCurso(id);
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /api/reportes/empresa/{id}
     * Obtiene reporte de una empresa
     */
    @GetMapping("/reportes/empresa/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<ReporteEmpresaDTO> getReporteEmpresa(@PathVariable Long id) {
        ReporteEmpresaDTO reporte = reportsService.getReporteEmpresa(id);
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /api/reportes/tutor-practicas/{id}
     * Obtiene reporte de un tutor de prácticas
     */
    @GetMapping("/reportes/tutor-practicas/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<ReporteTutorPracticasDTO> getReporteTutorPracticas(@PathVariable Long id) {
        ReporteTutorPracticasDTO reporte = reportsService.getReporteTutorPracticas(id);
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /api/reportes/alumno/{id}
     * Obtiene reporte de un alumno
     */
    @GetMapping("/reportes/alumno/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReporteAlumnoDTO> getReporteAlumno(@PathVariable Long id) {
        ReporteAlumnoDTO reporte = reportsService.getReporteAlumno(id);
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /api/reportes/ejecutivo
     * Obtiene reporte ejecutivo
     */
    @GetMapping("/reportes/ejecutivo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReporteEjecutivoDTO> getReporteEjecutivo() {
        ReporteEjecutivoDTO reporte = reportsService.getReporteEjecutivo();
        return ResponseEntity.ok(reporte);
    }

    // ========================= UTILIDADES ========================= //

    /**
     * GET /api/health
     * Health check del API
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", java.time.LocalDateTime.now());
        health.put("service", "Gestión de Prácticas API");
        return ResponseEntity.ok(health);
    }

    /**
     * GET /api/dashboard-data
     * Obtiene datos consolidados para el dashboard según el rol del usuario
     */
    @GetMapping("/dashboard-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // TODO: Personalizar según el rol del usuario autenticado
        data.put("totalAlumnos", alumnoService.getAllAlumnos().size());
        data.put("alumnosActivos", alumnoService.getAlumnosActivos().size());
        
        return ResponseEntity.ok(data);
    }

    /**
     * POST /api/calcular-nota/{alumnoId}
     * Calcula la nota final de un alumno
     */
    @PostMapping("/calcular-nota/{alumnoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<Map<String, Object>> calcularNotaFinal(@PathVariable Long alumnoId) {
        java.math.BigDecimal notaFinal = evaluacionService.calcularNotaFinalAlumno(alumnoId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("alumnoId", alumnoId);
        response.put("notaFinal", notaFinal);
        
        // Determinar calificación textual
        String calificacion;
        if (notaFinal.compareTo(java.math.BigDecimal.valueOf(9.0)) >= 0) {
            calificacion = "Sobresaliente";
        } else if (notaFinal.compareTo(java.math.BigDecimal.valueOf(7.0)) >= 0) {
            calificacion = "Notable";
        } else if (notaFinal.compareTo(java.math.BigDecimal.valueOf(6.0)) >= 0) {
            calificacion = "Bien";
        } else if (notaFinal.compareTo(java.math.BigDecimal.valueOf(5.0)) >= 0) {
            calificacion = "Suficiente";
        } else {
            calificacion = "Insuficiente";
        }
        
        response.put("calificacion", calificacion);
        response.put("aprobado", notaFinal.compareTo(java.math.BigDecimal.valueOf(5.0)) >= 0);
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/validar-dni
     * Valida formato de DNI español
     */
    @PostMapping("/validar-dni")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> validarDni(@RequestBody Map<String, String> request) {
        String dni = request.get("dni");
        boolean valido = validarFormatoDni(dni);
        
        Map<String, Object> response = new HashMap<>();
        response.put("dni", dni);
        response.put("valido", valido);
        
        return ResponseEntity.ok(response);
    }

    // ========================= MÉTODOS AUXILIARES ========================= //

    /**
     * Valida formato básico de DNI español
     */
    private boolean validarFormatoDni(String dni) {
        if (dni == null || dni.length() != 9) {
            return false;
        }
        
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        String numero = dni.substring(0, 8);
        char letra = dni.charAt(8);
        
        try {
            int numDni = Integer.parseInt(numero);
            return letras.charAt(numDni % 23) == Character.toUpperCase(letra);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}