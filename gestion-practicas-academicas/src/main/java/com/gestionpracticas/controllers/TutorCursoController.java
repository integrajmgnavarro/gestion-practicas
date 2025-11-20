package com.gestionpracticas.controllers;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.models.Usuario;
import com.gestionpracticas.repositories.UsuarioRepository;
import com.gestionpracticas.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tutor-curso")
@PreAuthorize("hasRole('TUTOR_CURSO')")
@RequiredArgsConstructor
public class TutorCursoController {

    private final TutorCursoService tutorService;
    private final AlumnoService alumnoService;
    private final EvaluacionService evaluacionService;
    private final EstadisticasService estadisticasService;
    private final ReportsService reportsService;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaService empresaService; // Servicio de Empresa añadido
    private final TutorPracticasService tutorPracticasService; // Servicio de TutorPrácticas añadido

    // ========================= DASHBOARD ========================= //

    /**
     * GET /tutor-curso/dashboard
     * Dashboard principal del tutor de curso
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorCurso(tutor.getId());

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("totalAlumnos", alumnos.size());
        model.addAttribute("alumnosActivos", alumnos.stream().filter(AlumnoDTO::getActivo).count());

        return "tutor-curso/dashboard";
    }

    // ========================= GESTIÓN DE ALUMNOS ========================= //

    /**
     * GET /tutor-curso/alumnos
     * Lista de alumnos de sus cursos
     */
    @GetMapping("/alumnos")
    public String alumnos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorCurso(tutor.getId());

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumnos", alumnos);

        return "tutor-curso/alumnos";
    }

    /**
     * GET /tutor-curso/alumnos/{id}
     * Detalle de un alumno específico
     */
    @GetMapping("/alumnos/{id}")
    public String alumnoDetalle(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        AlumnoDTO alumno = alumnoService.getAlumnoById(id);

        // Verificar que el alumno pertenece a un curso de este tutor
        List<AlumnoDTO> alumnosTutor = alumnoService.getAlumnosByTutorCurso(tutor.getId());
        boolean perteneceAlTutor = alumnosTutor.stream().anyMatch(a -> a.getId().equals(id));

        if (!perteneceAlTutor) {
            return "redirect:/tutor-curso/alumnos?error=unauthorized";
        }

        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByAlumnoId(id);
        ReporteAlumnoDTO reporte = reportsService.getReporteAlumno(id);

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumno", alumno);
        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("reporte", reporte);

        return "tutor-curso/alumno-detalle";
    }

    // ========================= EMPRESAS Y TUTORES DE PRÁCTICAS ========================= //

    /**
     * GET /tutor-curso/empresas
     * Lista de empresas asociadas a sus alumnos
     */
    @GetMapping("/empresas")
    public String empresas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorCurso(tutor.getId());

        // Obtener IDs de empresas únicas de los alumnos
        List<Long> empresaIds = alumnos.stream()
                .filter(a -> a.getEmpresaId() != null)
                .map(AlumnoDTO::getEmpresaId)
                .distinct()
                .toList();

        // Cargar datos completos de empresas (Resuelve el TODO)
        List<EmpresaDTO> empresas = empresaIds.stream()
                .map(empresaService::getEmpresaById)
                .toList();

        model.addAttribute("tutor", tutor);
        model.addAttribute("empresas", empresas); // Ahora se añade la lista de DTOs completa

        return "tutor-curso/empresas";
    }

    /**
     * GET /tutor-curso/tutores-practicas
     * Lista de tutores de prácticas de sus alumnos
     */
    @GetMapping("/tutores-practicas")
    public String tutoresPracticas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorCurso(tutor.getId());

        // Obtener IDs de tutores de prácticas únicos
        List<Long> tutorPracticasIds = alumnos.stream()
                .filter(a -> a.getTutorPracticasId() != null)
                .map(AlumnoDTO::getTutorPracticasId)
                .distinct()
                .toList();

        // Cargar datos completos de tutores de prácticas (Resuelve el TODO)
        List<TutorPracticasDTO> tutoresPracticas = tutorPracticasIds.stream()
                .map(tutorPracticasService::getTutorPracticasById)
                .toList();

        model.addAttribute("tutor", tutor);
        model.addAttribute("tutoresPracticas", tutoresPracticas); // Ahora se añade la lista de DTOs completa

        return "tutor-curso/tutores-practicas";
    }

    // ========================= EVALUACIONES DE TUTORES ========================= //

    /**
     * GET /tutor-curso/evaluaciones-tutores
     * Lista de evaluaciones de tutores realizadas
     */
    @GetMapping("/evaluaciones-tutores")
    public String evaluacionesTutores(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        List<EvaluacionTutorDTO> evaluaciones = evaluacionService.getEvaluacionesTutorByTutorCursoId(tutor.getId());

        model.addAttribute("tutor", tutor);
        model.addAttribute("evaluaciones", evaluaciones);

        return "tutor-curso/evaluaciones-tutores";
    }

    /**
     * GET /tutor-curso/evaluaciones-tutores/nueva
     * Formulario para evaluar un tutor de prácticas
     */
    @GetMapping("/evaluaciones-tutores/nueva")
    public String nuevaEvaluacionTutor(@RequestParam(required = false) Long tutorPracticasId,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        
        // Obtener IDs de tutores de prácticas de sus alumnos
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorCurso(tutor.getId());
        List<Long> tutorPracticasIds = alumnos.stream()
                .filter(a -> a.getTutorPracticasId() != null)
                .map(AlumnoDTO::getTutorPracticasId)
                .distinct()
                .toList();

        EvaluacionTutorCreateDTO createDTO = new EvaluacionTutorCreateDTO();
        createDTO.setTutorCursoId(tutor.getId());
        if (tutorPracticasId != null) {
            createDTO.setTutorPracticasId(tutorPracticasId);
        }

        model.addAttribute("tutor", tutor);
        model.addAttribute("tutorPracticasIds", tutorPracticasIds);
        model.addAttribute("evaluacionTutorCreateDTO", createDTO);

        return "tutor-curso/evaluacion-tutor-form";
    }

    /**
     * POST /tutor-curso/evaluaciones-tutores
     * Crea una evaluación de tutor
     */
    @PostMapping("/evaluaciones-tutores")
    public String createEvaluacionTutor(@Valid @ModelAttribute EvaluacionTutorCreateDTO createDTO,
                                        BindingResult result,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        Model model) {
        if (result.hasErrors()) {
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());

            // Es necesario volver a cargar los IDs de tutores de prácticas en caso de error de validación
            List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorCurso(tutor.getId());
            List<Long> tutorPracticasIds = alumnos.stream()
                .filter(a -> a.getTutorPracticasId() != null)
                .map(AlumnoDTO::getTutorPracticasId)
                .distinct()
                .toList();

            model.addAttribute("tutor", tutor);
            model.addAttribute("tutorPracticasIds", tutorPracticasIds);
            return "tutor-curso/evaluacion-tutor-form";
        }

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());

        createDTO.setTutorCursoId(tutor.getId());
        evaluacionService.createEvaluacionTutor(createDTO);

        return "redirect:/tutor-curso/evaluaciones-tutores?success=true";
    }

    // ========================= ESTADÍSTICAS ========================= //

    /**
     * GET /tutor-curso/estadisticas
     * Estadísticas de sus cursos
     */
    @GetMapping("/estadisticas")
    public String estadisticas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        
        // TODO: Filtrar estadísticas solo de sus cursos (se mantiene el TODO)
        EstadisticasGeneralesDTO estadisticas = estadisticasService.getEstadisticasGenerales();

        model.addAttribute("tutor", tutor);
        model.addAttribute("estadisticas", estadisticas);

        return "tutor-curso/estadisticas";
    }

    // ========================= REPORTES ========================= //

    /**
     * GET /tutor-curso/reportes
     * Página de generación de reportes
     */
    @GetMapping("/reportes")
    public String reportes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorCurso(tutor.getId());

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumnos", alumnos);

        return "tutor-curso/reportes";
    }

    /**
     * GET /tutor-curso/reportes/alumno/{id}
     * Genera reporte de un alumno
     */
    @GetMapping("/reportes/alumno/{id}")
    public String reporteAlumno(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        ReporteAlumnoDTO reporte = reportsService.getReporteAlumno(id);

        model.addAttribute("tutor", tutor);
        model.addAttribute("reporte", reporte);

        return "tutor-curso/reporte-alumno";
    }

    // ========================= API REST ========================= //

    /**
     * GET /tutor-curso/alumnos/api
     * Lista de alumnos (JSON)
     */
    @GetMapping("/alumnos/api")
    @ResponseBody
    public ResponseEntity<List<AlumnoDTO>> getAlumnosApi(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorCurso(tutor.getId());

        return ResponseEntity.ok(alumnos);
    }

    /**
     * GET /tutor-curso/estadisticas/api
     * Estadísticas (JSON)
     */
    @GetMapping("/estadisticas/api")
    @ResponseBody
    public ResponseEntity<EstadisticasGeneralesDTO> getEstadisticasApi(@AuthenticationPrincipal UserDetails userDetails) {
        EstadisticasGeneralesDTO estadisticas = estadisticasService.getEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * POST /tutor-curso/evaluaciones-tutores/api
     * Crea una evaluación de tutor (JSON)
     */
    @PostMapping("/evaluaciones-tutores/api")
    @ResponseBody
    public ResponseEntity<EvaluacionTutorDTO> createEvaluacionTutorApi(@Valid @RequestBody EvaluacionTutorCreateDTO createDTO,
                                                                         @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorCursoDTO tutor = tutorService.findById(usuario.getReferenceId());
        createDTO.setTutorCursoId(tutor.getId());

        EvaluacionTutorDTO evaluacion = evaluacionService.createEvaluacionTutor(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluacion);
    }
}
