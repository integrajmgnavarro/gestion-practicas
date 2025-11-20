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

/**
 * Controlador para la gestión de funcionalidades exclusivas del Tutor de Prácticas.
 * Todas las rutas requieren que el usuario autenticado tenga el rol 'TUTOR_PRACTICAS'.
 */
@Controller
@RequestMapping("/tutor-practicas")
@PreAuthorize("hasRole('TUTOR_PRACTICAS')")
@RequiredArgsConstructor
public class TutorPracticasController {

    private final TutorPracticasService tutorPracticasService;
    private final AlumnoService alumnoService;
    private final EvaluacionService evaluacionService;
    private final UsuarioRepository usuarioRepository;
    // Servicios para Observaciones e Incidencias
    private final ObservacionDiariaService observacionDiariaService;
    private final IncidenciaService incidenciaService;


    /**
     * Método auxiliar para obtener la entidad TutorPracticas a partir de los detalles del usuario autenticado.
     * @param userDetails Los detalles del usuario autenticado proporcionados por Spring Security.
     * @return El DTO del Tutor de Prácticas.
     */
    private TutorPracticasDTO getCurrentTutorPracticas(UserDetails userDetails) {
        // 1. Encontrar la entidad Usuario por el email (username)
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // 2. Usar el referenceId del Usuario (que apunta al ID del TutorPracticas) para obtener el DTO
        return tutorPracticasService.getTutorPracticasById(usuario.getReferenceId());
    }

    // ========================= DASHBOARD ========================= //

    /**
     * GET /tutor-practicas/dashboard
     * Dashboard principal del tutor de prácticas.
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("totalAlumnos", alumnos.size());
        model.addAttribute("alumnosActivos", alumnos.stream().filter(AlumnoDTO::getActivo).count());

        return "tutor-practicas/dashboard";
    }

    // ========================= GESTIÓN DE ALUMNOS ========================= //

    /**
     * GET /tutor-practicas/alumnos
     * Lista de alumnos asignados al tutor.
     */
    @GetMapping("/alumnos")
    public String alumnos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumnos", alumnos);

        return "tutor-practicas/alumnos";
    }

    /**
     * GET /tutor-practicas/alumnos/{id}
     * Detalle de un alumno específico.
     */
    @GetMapping("/alumnos/{id}")
    public String alumnoDetalle(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);
        AlumnoDTO alumno = alumnoService.getAlumnoById(id);

        // Seguridad: Verificar que el alumno existe y pertenece a este tutor
        if (alumno == null || !alumno.getTutorPracticasId().equals(tutor.getId())) {
            // Redirige o muestra una página de error si no está autorizado
            return "redirect:/tutor-practicas/alumnos?error=unauthorized";
        }

        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByAlumnoId(id);

        // Obtener observaciones e incidencias de este alumno
        List<ObservacionDiariaDTO> observaciones = observacionDiariaService.getObservacionesByAlumnoId(id);
        // CORRECCIÓN: Se ha corregido el error tipográfico en el nombre de la variable de 'ncias' a 'incidencias'
        List<IncidenciaDTO> incidencias = incidenciaService.getIncidenciasByAlumnoId(id);

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumno", alumno);
        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("observaciones", observaciones);
        model.addAttribute("incidencias", incidencias);

        return "tutor-practicas/alumno-detalle";
    }

    // ========================= EVALUACIONES ========================= //

    /**
     * GET /tutor-practicas/evaluaciones
     * Página de evaluaciones.
     */
    @GetMapping("/evaluaciones")
    public String evaluaciones(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);
        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByTutorPracticasId(tutor.getId());

        model.addAttribute("tutor", tutor);
        model.addAttribute("evaluaciones", evaluaciones);

        return "tutor-practicas/evaluaciones";
    }

    /**
     * GET /tutor-practicas/evaluaciones/nueva
     * Formulario para crear nueva evaluación.
     * @param alumnoId ID del alumno, opcional para precargar el formulario.
     */
    @GetMapping("/evaluaciones/nueva")
    public String nuevaEvaluacion(@RequestParam(required = false) Long alumnoId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);
        // Se cargan los alumnos asignados para el dropdown
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());
        // Se cargan las capacidades para el formulario
        List<CapacidadEvaluacionDTO> capacidades = evaluacionService.getAllCapacidadesEvaluacion();

        EvaluacionCreateDTO createDTO = new EvaluacionCreateDTO();
        if (alumnoId != null) {
            createDTO.setAlumnoId(alumnoId);
        }
        createDTO.setTutorPracticasId(tutor.getId()); // Se precarga el ID del tutor

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("capacidades", capacidades);
        model.addAttribute("evaluacionCreateDTO", createDTO);

        return "tutor-practicas/evaluacion-form";
    }

    /**
     * POST /tutor-practicas/evaluaciones
     * Crea una nueva evaluación.
     */
    @PostMapping("/evaluaciones")
    public String createEvaluacion(@Valid @ModelAttribute EvaluacionCreateDTO createDTO,
                                   BindingResult result,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);

        if (result.hasErrors()) {
            // Se recargan los datos necesarios en caso de error de validación
            List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());
            List<CapacidadEvaluacionDTO> capacidades = evaluacionService.getAllCapacidadesEvaluacion();

            model.addAttribute("tutor", tutor);
            model.addAttribute("alumnos", alumnos);
            model.addAttribute("capacidades", capacidades);
            return "tutor-practicas/evaluacion-form";
        }

        // Se asegura que el ID del tutor sea el correcto (incluso si se intenta manipular en el form)
        createDTO.setTutorPracticasId(tutor.getId());
        evaluacionService.createEvaluacion(createDTO);

        return "redirect:/tutor-practicas/evaluaciones?success=true";
    }

    // ========================= OBSERVACIONES DIARIAS ========================= //

    /**
     * GET /tutor-practicas/observaciones
     * Lista de observaciones diarias de los alumnos asignados.
     */
    @GetMapping("/observaciones")
    public String observaciones(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);

        // Obtener IDs de los alumnos del tutor
        List<Long> alumnoIds = alumnoService.getAlumnosByTutorPracticas(tutor.getId())
                .stream().map(AlumnoDTO::getId).toList();

        // Obtener observaciones usando la lista de IDs de alumnos
        List<ObservacionDiariaDTO> observaciones = observacionDiariaService.getObservacionesByAlumnoIds(alumnoIds);

        model.addAttribute("tutor", tutor);
        model.addAttribute("observaciones", observaciones);

        return "tutor-practicas/observaciones";
    }

    // ========================= INCIDENCIAS ========================= //

    /**
     * GET /tutor-practicas/incidencias
     * Lista de incidencias de los alumnos asignados.
     */
    @GetMapping("/incidencias")
    public String incidencias(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);

        // Obtener IDs de los alumnos del tutor
        List<Long> alumnoIds = alumnoService.getAlumnosByTutorPracticas(tutor.getId())
                .stream().map(AlumnoDTO::getId).toList();

        // Obtener incidencias usando la lista de IDs de alumnos
        List<IncidenciaDTO> incidencias = incidenciaService.getIncidenciasByAlumnoIds(alumnoIds);

        model.addAttribute("tutor", tutor);
        model.addAttribute("incidencias", incidencias);

        return "tutor-practicas/incidencias";
    }

    /**
     * GET /tutor-practicas/incidencias/nueva
     * Formulario para crear nueva incidencia.
     * @param alumnoId ID del alumno, opcional para precargar el formulario.
     */
    @GetMapping("/incidencias/nueva")
    public String nuevaIncidencia(@RequestParam(required = false) Long alumnoId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());

        IncidenciaCreateDTO createDTO = new IncidenciaCreateDTO();
        if (alumnoId != null) {
            createDTO.setAlumnoId(alumnoId);
        }
        createDTO.setTutorPracticasId(tutor.getId()); // Se precarga el ID del tutor

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("incidenciaCreateDTO", createDTO);

        return "tutor-practicas/incidencia-form";
    }

    /**
     * POST /tutor-practicas/incidencias
     * Crea una nueva incidencia.
     */
    @PostMapping("/incidencias")
    public String createIncidencia(@Valid @ModelAttribute IncidenciaCreateDTO createDTO,
                                   BindingResult result,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);

        if (result.hasErrors()) {
            List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());

            model.addAttribute("tutor", tutor);
            model.addAttribute("alumnos", alumnos);
            return "tutor-practicas/incidencia-form";
        }

        // Se asegura que el ID del tutor sea el correcto
        createDTO.setTutorPracticasId(tutor.getId());
        incidenciaService.createIncidencia(createDTO);

        return "redirect:/tutor-practicas/incidencias?success=true";
    }

    // ========================= API REST ========================= //

    /**
     * GET /tutor-practicas/alumnos/api
     * Lista de alumnos (JSON).
     * @return Lista de AlumnoDTO.
     */
    @GetMapping("/alumnos/api")
    @ResponseBody
    public ResponseEntity<List<AlumnoDTO>> getAlumnosApi(@AuthenticationPrincipal UserDetails userDetails) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());

        return ResponseEntity.ok(alumnos);
    }

    /**
     * POST /tutor-practicas/evaluaciones/api
     * Crea una evaluación (JSON).
     * @param createDTO Datos de la evaluación a crear.
     * @return El DTO de la evaluación creada con estado HTTP 201.
     */
    @PostMapping("/evaluaciones/api")
    @ResponseBody
    public ResponseEntity<EvaluacionDTO> createEvaluacionApi(@Valid @RequestBody EvaluacionCreateDTO createDTO,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        TutorPracticasDTO tutor = getCurrentTutorPracticas(userDetails);

        // Se asegura que el ID del tutor sea el correcto antes de crear
        createDTO.setTutorPracticasId(tutor.getId());

        EvaluacionDTO evaluacion = evaluacionService.createEvaluacion(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluacion);
    }
}
