package com.gestionpracticas.controllers;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.services.AlumnoService;
import com.gestionpracticas.services.EstadisticasService;
import com.gestionpracticas.services.EvaluacionService;
import com.gestionpracticas.services.ReportsService;
import com.gestionpracticas.services.TutorCursoService;
import com.gestionpracticas.services.TutorPracticasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gestionpracticas.exception.ResourceNotFoundException;
import java.math.BigDecimal;
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
    // Servicios específicos de Tutores
    private final TutorCursoService tutorCursoService;
    private final TutorPracticasService tutorPracticasService;
    // Otros servicios
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
     * GET /api/alumnos/tutor-practicas/{tutorPracticasId}
     * Obtiene alumnos por Tutor de Prácticas
     */
    @GetMapping("/alumnos/tutor-practicas/{tutorPracticasId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<List<AlumnoDTO>> getAlumnosByTutorPracticas(@PathVariable Long tutorPracticasId) {
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutorPracticasId);
        return ResponseEntity.ok(alumnos);
    }
    
    /**
     * GET /api/alumnos/tutor-curso/{tutorCursoId}
     * Obtiene alumnos por Tutor de Curso
     */
    @GetMapping("/alumnos/tutor-curso/{tutorCursoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<List<AlumnoDTO>> getAlumnosByTutorCurso(@PathVariable Long tutorCursoId) {
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorCurso(tutorCursoId);
        return ResponseEntity.ok(alumnos);
    }


    /**
     * GET /api/alumnos/buscar
     * Búsqueda de alumnos con criterios (llama al método adaptado en AlumnoService)
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
     * CORRECCIÓN: Se inyecta el ID en el DTO y se llama al service con un solo argumento,
     * ya que AlumnoService.updateAlumno() solo acepta el DTO.
     */
    @PutMapping("/alumnos/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO')")
    public ResponseEntity<AlumnoDTO> updateAlumno(@PathVariable Long id,
            @Valid @RequestBody AlumnoUpdateDTO updateDTO) {
        // Se asume que AlumnoService.updateAlumno(DTO) espera que el ID esté en el DTO.
        updateDTO.setId(id);
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

    // ========================= TUTORES DE CURSO ========================= //

    /**
     * GET /api/tutor-curso
     * Lista todos los tutores de curso
     */
    @GetMapping("/tutor-curso")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TutorCursoDTO>> getAllTutoresCurso() {
        List<TutorCursoDTO> tutores = tutorCursoService.findAllList();
        return ResponseEntity.ok(tutores);
    }

    /**
     * GET /api/tutor-curso/{id}
     * Obtiene un tutor de curso por ID
     */
    @GetMapping("/tutor-curso/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<TutorCursoDTO> getTutorCursoById(@PathVariable Long id) {
        TutorCursoDTO tutor = tutorCursoService.findById(id);
        return ResponseEntity.ok(tutor);
    }


    /**
     * POST /api/tutor-curso
     * Crea un tutor de curso
     */
    @PostMapping("/tutor-curso")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TutorCursoDTO> createTutorCurso(@Valid @RequestBody TutorCursoCreateDTO createDTO) {
        TutorCursoDTO tutor = tutorCursoService.createTutorCurso(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(tutor);
    }

    /**
     * PUT /api/tutor-curso/{id}
     * Actualiza un tutor de curso
     * NOTA: La firma de TutorCursoService.updateTutorCurso(Long, DTO) se mantiene con dos argumentos.
     */
    @PutMapping("/tutores-curso/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TutorCursoDTO> updateTutorCurso(
            @PathVariable Long id,
            @Valid @RequestBody TutorCursoUpdateDTO updateDTO) {
        
        try {
            // 1. Asignar el ID del PathVariable al DTO para que el Service lo use.
            //    (Esto usa 'updateDTO', que es el nombre correcto del parámetro)
            updateDTO.setId(id);
            
            // 2. CORRECCIÓN CLAVE: Llama al servicio SOLAMENTE con el DTO (updateDTO).
            TutorCursoDTO tutor = tutorCursoService.updateTutorCurso(updateDTO);
            
            return ResponseEntity.ok(tutor);
        } catch (ResourceNotFoundException e) {
            // Manejo de recursos no encontrados
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Manejo de errores de unicidad/negocio (DuplicateResourceException, etc.)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * DELETE /api/tutor-curso/{id}
     * Elimina un tutor de curso
     */
    @DeleteMapping("/tutor-curso/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTutorCurso(@PathVariable Long id) {
        tutorCursoService.deleteTutorCurso(id);
        return ResponseEntity.noContent().build();
    }

    // ========================= TUTORES DE PRÁCTICAS ========================= //
    
    /**
     * GET /api/tutores-practicas
     * Lista todos los tutores de prácticas
     */
    @GetMapping("/tutores-practicas")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<List<TutorPracticasDTO>> getAllTutoresPracticas() {
        List<TutorPracticasDTO> tutores = tutorPracticasService.getAllTutoresPracticas();
        return ResponseEntity.ok(tutores);
    }
    
    /**
     * GET /api/tutores-practicas/{id}
     * Obtiene un tutor de prácticas por ID
     */
    @GetMapping("/tutores-practicas/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<TutorPracticasDTO> getTutorPracticasById(@PathVariable Long id) {
        TutorPracticasDTO tutor = tutorPracticasService.getTutorPracticasById(id);
        return ResponseEntity.ok(tutor);
    }


    /**
     * POST /api/tutores-practicas
     * Crea un tutor de prácticas
     */
    @PostMapping("/tutores-practicas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TutorPracticasDTO> createTutorPracticas(@Valid @RequestBody TutorPracticasCreateDTO createDTO) {
        TutorPracticasDTO tutor = tutorPracticasService.createTutorPracticas(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(tutor);
    }
    
    /**
     * PUT /api/tutores-practicas/{id}
     * Actualiza un tutor de prácticas
     * CORRECCIÓN: Se inyecta el ID en el DTO y se llama al service con un solo argumento.
     */
    @PutMapping("/tutores-practicas/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TutorPracticasDTO> updateTutorPracticas(@PathVariable Long id,
                                                                 @Valid @RequestBody TutorPracticasUpdateDTO updateDTO) {
        // Se asume que TutorPracticasService.updateTutorPracticas(DTO) espera que el ID esté en el DTO.
        updateDTO.setId(id);
        TutorPracticasDTO tutor = tutorPracticasService.updateTutorPracticas(id, updateDTO);
        return ResponseEntity.ok(tutor);
    }

    /**
     * DELETE /api/tutores-practicas/{id}
     * Elimina un tutor de prácticas
     */
    @DeleteMapping("/tutores-practicas/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTutorPracticas(@PathVariable Long id) {
        tutorPracticasService.deleteTutorPracticas(id);
        return ResponseEntity.noContent().build();
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
        // Se asume que EvaluacionService.updateEvaluacion() acepta dos argumentos.
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
        if (notaFinal.compareTo(BigDecimal.valueOf(9.0)) >= 0) {
            calificacion = "Sobresaliente";
        } else if (notaFinal.compareTo(BigDecimal.valueOf(7.0)) >= 0) {
            calificacion = "Notable";
        } else if (notaFinal.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
            calificacion = "Aprobado";
        } else {
            calificacion = "Suspenso";
        }
        response.put("calificacion", calificacion);
        
        return ResponseEntity.ok(response);
    }
}