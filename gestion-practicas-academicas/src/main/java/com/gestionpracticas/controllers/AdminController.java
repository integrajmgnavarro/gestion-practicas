package com.gestionpracticas.controllers;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AlumnoService alumnoService;
    private final TutorService tutorService;
    private final EvaluacionService evaluacionService;
    private final EstadisticasService estadisticasService;
    private final ReportsService reportsService;
    // TODO: Añadir cuando se creen: CursoService, EmpresaService, UsuarioService

    // ========================= DASHBOARD ========================= //

    /**
     * GET /admin/dashboard
     * Dashboard principal del administrador
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        EstadisticasGeneralesDTO estadisticas = estadisticasService.getEstadisticasGenerales();
        ReporteEjecutivoDTO reporteEjecutivo = reportsService.getReporteEjecutivo();

        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("reporteEjecutivo", reporteEjecutivo);

        return "admin/dashboard";
    }

    // ========================= GESTIÓN DE ALUMNOS ========================= //

    /**
     * GET /admin/alumnos
     * Lista de todos los alumnos
     */
    @GetMapping("/alumnos")
    public String alumnos(Model model) {
        List<AlumnoDTO> alumnos = alumnoService.getAllAlumnos();
        model.addAttribute("alumnos", alumnos);
        return "admin/alumnos";
    }

    /**
     * GET /admin/alumnos/nuevo
     * Formulario para crear nuevo alumno
     */
    @GetMapping("/alumnos/nuevo")
    public String nuevoAlumno(Model model) {
        model.addAttribute("alumnoCreateDTO", new AlumnoCreateDTO());
        // TODO: Cargar listas de cursos, empresas, tutores
        return "admin/alumno-form";
    }

    /**
     * POST /admin/alumnos
     * Crea un nuevo alumno
     */
    @PostMapping("/alumnos")
    public String createAlumno(@Valid @ModelAttribute AlumnoCreateDTO createDTO,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "admin/alumno-form";
        }

        alumnoService.createAlumno(createDTO);
        return "redirect:/admin/alumnos?success=true";
    }

    /**
     * GET /admin/alumnos/{id}/editar
     * Formulario para editar alumno
     */
    @GetMapping("/alumnos/{id}/editar")
    public String editarAlumno(@PathVariable Long id, Model model) {
        AlumnoDTO alumno = alumnoService.getAlumnoById(id);
        model.addAttribute("alumno", alumno);
        model.addAttribute("alumnoUpdateDTO", new AlumnoUpdateDTO());
        return "admin/alumno-edit";
    }

    /**
     * PUT /admin/alumnos/{id}
     * Actualiza un alumno
     */
    @PutMapping("/alumnos/{id}")
    public String updateAlumno(@PathVariable Long id,
                              @Valid @ModelAttribute AlumnoUpdateDTO updateDTO,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            AlumnoDTO alumno = alumnoService.getAlumnoById(id);
            model.addAttribute("alumno", alumno);
            return "admin/alumno-edit";
        }

        alumnoService.updateAlumno(id, updateDTO);
        return "redirect:/admin/alumnos?success=true";
    }

    /**
     * DELETE /admin/alumnos/{id}
     * Elimina un alumno
     */
    @DeleteMapping("/alumnos/{id}")
    public String deleteAlumno(@PathVariable Long id) {
        alumnoService.deleteAlumno(id);
        return "redirect:/admin/alumnos?deleted=true";
    }

    // ========================= GESTIÓN DE TUTORES DE CURSO ========================= //

    /**
     * GET /admin/tutores-curso
     * Lista de tutores de curso
     */
    @GetMapping("/tutores-curso")
    public String tutoresCurso(Model model) {
        List<TutorCursoDTO> tutores = tutorService.getAllTutoresCurso();
        model.addAttribute("tutores", tutores);
        return "admin/tutores-curso";
    }

    /**
     * GET /admin/tutores-curso/nuevo
     * Formulario para crear tutor de curso
     */
    @GetMapping("/tutores-curso/nuevo")
    public String nuevoTutorCurso(Model model) {
        model.addAttribute("tutorCursoCreateDTO", new TutorCursoCreateDTO());
        return "admin/tutor-curso-form";
    }

    /**
     * POST /admin/tutores-curso
     * Crea un tutor de curso
     */
    @PostMapping("/tutores-curso")
    public String createTutorCurso(@Valid @ModelAttribute TutorCursoCreateDTO createDTO,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            return "admin/tutor-curso-form";
        }

        tutorService.createTutorCurso(createDTO);
        return "redirect:/admin/tutores-curso?success=true";
    }

    /**
     * DELETE /admin/tutores-curso/{id}
     * Elimina un tutor de curso
     */
    @DeleteMapping("/tutores-curso/{id}")
    public String deleteTutorCurso(@PathVariable Long id) {
        tutorService.deleteTutorCurso(id);
        return "redirect:/admin/tutores-curso?deleted=true";
    }

    // ========================= GESTIÓN DE TUTORES DE PRÁCTICAS ========================= //

    /**
     * GET /admin/tutores-practicas
     * Lista de tutores de prácticas
     */
    @GetMapping("/tutores-practicas")
    public String tutoresPracticas(Model model) {
        List<TutorPracticasDTO> tutores = tutorService.getAllTutoresPracticas();
        model.addAttribute("tutores", tutores);
        return "admin/tutores-practicas";
    }

    /**
     * GET /admin/tutores-practicas/nuevo
     * Formulario para crear tutor de prácticas
     */
    @GetMapping("/tutores-practicas/nuevo")
    public String nuevoTutorPracticas(Model model) {
        model.addAttribute("tutorPracticasCreateDTO", new TutorPracticasCreateDTO());
        // TODO: Cargar lista de empresas
        return "admin/tutor-practicas-form";
    }

    /**
     * POST /admin/tutores-practicas
     * Crea un tutor de prácticas
     */
    @PostMapping("/tutores-practicas")
    public String createTutorPracticas(@Valid @ModelAttribute TutorPracticasCreateDTO createDTO,
                                       BindingResult result,
                                       Model model) {
        if (result.hasErrors()) {
            return "admin/tutor-practicas-form";
        }

        tutorService.createTutorPracticas(createDTO);
        return "redirect:/admin/tutores-practicas?success=true";
    }

    /**
     * DELETE /admin/tutores-practicas/{id}
     * Elimina un tutor de prácticas
     */
    @DeleteMapping("/tutores-practicas/{id}")
    public String deleteTutorPracticas(@PathVariable Long id) {
        tutorService.deleteTutorPracticas(id);
        return "redirect:/admin/tutores-practicas?deleted=true";
    }

    // ========================= GESTIÓN DE CURSOS ========================= //

    /**
     * GET /admin/cursos
     * Lista de cursos
     */
    @GetMapping("/cursos")
    public String cursos(Model model) {
        // TODO: Implementar CursoService
        // List<CursoDTO> cursos = cursoService.getAllCursos();
        // model.addAttribute("cursos", cursos);
        return "admin/cursos";
    }

    /**
     * GET /admin/cursos/nuevo
     * Formulario para crear curso
     */
    @GetMapping("/cursos/nuevo")
    public String nuevoCurso(Model model) {
        model.addAttribute("cursoCreateDTO", new CursoCreateDTO());
        return "admin/curso-form";
    }

    /**
     * POST /admin/cursos
     * Crea un curso
     */
    @PostMapping("/cursos")
    public String createCurso(@Valid @ModelAttribute CursoCreateDTO createDTO,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "admin/curso-form";
        }

        // TODO: cursoService.createCurso(createDTO);
        return "redirect:/admin/cursos?success=true";
    }

    // ========================= GESTIÓN DE EMPRESAS ========================= //

    /**
     * GET /admin/empresas
     * Lista de empresas
     */
    @GetMapping("/empresas")
    public String empresas(Model model) {
        // TODO: Implementar EmpresaService
        // List<EmpresaDTO> empresas = empresaService.getAllEmpresas();
        // model.addAttribute("empresas", empresas);
        return "admin/empresas";
    }

    /**
     * GET /admin/empresas/nuevo
     * Formulario para crear empresa
     */
    @GetMapping("/empresas/nuevo")
    public String nuevaEmpresa(Model model) {
        model.addAttribute("empresaCreateDTO", new EmpresaCreateDTO());
        return "admin/empresa-form";
    }

    /**
     * POST /admin/empresas
     * Crea una empresa
     */
    @PostMapping("/empresas")
    public String createEmpresa(@Valid @ModelAttribute EmpresaCreateDTO createDTO,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "admin/empresa-form";
        }

        // TODO: empresaService.createEmpresa(createDTO);
        return "redirect:/admin/empresas?success=true";
    }

    // ========================= GESTIÓN DE CRITERIOS DE EVALUACIÓN ========================= //

    /**
     * GET /admin/criterios-evaluacion
     * Lista de criterios de evaluación
     */
    @GetMapping("/criterios-evaluacion")
    public String criteriosEvaluacion(Model model) {
        List<CriterioEvaluacionDTO> criterios = evaluacionService.getAllCriteriosEvaluacion();
        model.addAttribute("criterios", criterios);
        return "admin/criterios-evaluacion";
    }

    /**
     * GET /admin/criterios-evaluacion/nuevo
     * Formulario para crear criterio
     */
    @GetMapping("/criterios-evaluacion/nuevo")
    public String nuevoCriterio(Model model) {
        model.addAttribute("criterioCreateDTO", new CriterioEvaluacionCreateDTO());
        return "admin/criterio-form";
    }

    /**
     * POST /admin/criterios-evaluacion
     * Crea un criterio de evaluación
     */
    @PostMapping("/criterios-evaluacion")
    public String createCriterio(@Valid @ModelAttribute CriterioEvaluacionCreateDTO createDTO,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            return "admin/criterio-form";
        }

        evaluacionService.createCriterioEvaluacion(createDTO);
        return "redirect:/admin/criterios-evaluacion?success=true";
    }

    /**
     * GET /admin/capacidades-evaluacion
     * Lista de capacidades de evaluación
     */
    @GetMapping("/capacidades-evaluacion")
    public String capacidadesEvaluacion(Model model) {
        List<CapacidadEvaluacionDTO> capacidades = evaluacionService.getAllCapacidadesEvaluacion();
        model.addAttribute("capacidades", capacidades);
        return "admin/capacidades-evaluacion";
    }

    // ========================= ESTADÍSTICAS ========================= //

    /**
     * GET /admin/estadisticas
     * Estadísticas completas del sistema
     */
    @GetMapping("/estadisticas")
    public String estadisticas(Model model) {
        EstadisticasGeneralesDTO estadisticas = estadisticasService.getEstadisticasGenerales();
        EstadisticasEmpleabilidadDTO empleabilidad = estadisticasService.getEstadisticasEmpleabilidad();

        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("empleabilidad", empleabilidad);

        return "admin/estadisticas";
    }

    // ========================= REPORTES ========================= //

    /**
     * GET /admin/reportes
     * Página de generación de reportes
     */
    @GetMapping("/reportes")
    public String reportes(Model model) {
        return "admin/reportes";
    }

    /**
     * GET /admin/reportes/ejecutivo
     * Reporte ejecutivo
     */
    @GetMapping("/reportes/ejecutivo")
    public String reporteEjecutivo(Model model) {
        ReporteEjecutivoDTO reporte = reportsService.getReporteEjecutivo();
        model.addAttribute("reporte", reporte);
        return "admin/reporte-ejecutivo";
    }

    // ========================= API REST ========================= //

    /**
     * GET /admin/alumnos/api
     * Lista de alumnos (JSON)
     */
    @GetMapping("/alumnos/api")
    @ResponseBody
    public ResponseEntity<List<AlumnoDTO>> getAlumnosApi() {
        List<AlumnoDTO> alumnos = alumnoService.getAllAlumnos();
        return ResponseEntity.ok(alumnos);
    }

    /**
     * POST /admin/alumnos/api
     * Crea un alumno (JSON)
     */
    @PostMapping("/alumnos/api")
    @ResponseBody
    public ResponseEntity<AlumnoDTO> createAlumnoApi(@Valid @RequestBody AlumnoCreateDTO createDTO) {
        AlumnoDTO alumno = alumnoService.createAlumno(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(alumno);
    }

    /**
     * PUT /admin/alumnos/{id}/api
     * Actualiza un alumno (JSON)
     */
    @PutMapping("/alumnos/{id}/api")
    @ResponseBody
    public ResponseEntity<AlumnoDTO> updateAlumnoApi(@PathVariable Long id,
                                                     @Valid @RequestBody AlumnoUpdateDTO updateDTO) {
        AlumnoDTO alumno = alumnoService.updateAlumno(id, updateDTO);
        return ResponseEntity.ok(alumno);
    }

    /**
     * DELETE /admin/alumnos/{id}/api
     * Elimina un alumno (JSON)
     */
    @DeleteMapping("/alumnos/{id}/api")
    @ResponseBody
    public ResponseEntity<Void> deleteAlumnoApi(@PathVariable Long id) {
        alumnoService.deleteAlumno(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /admin/estadisticas/api
     * Estadísticas (JSON)
     */
    @GetMapping("/estadisticas/api")
    @ResponseBody
    public ResponseEntity<EstadisticasGeneralesDTO> getEstadisticasApi() {
        EstadisticasGeneralesDTO estadisticas = estadisticasService.getEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * GET /admin/reportes/ejecutivo/api
     * Reporte ejecutivo (JSON)
     */
    @GetMapping("/reportes/ejecutivo/api")
    @ResponseBody
    public ResponseEntity<ReporteEjecutivoDTO> getReporteEjecutivoApi() {
        ReporteEjecutivoDTO reporte = reportsService.getReporteEjecutivo();
        return ResponseEntity.ok(reporte);
    }
}