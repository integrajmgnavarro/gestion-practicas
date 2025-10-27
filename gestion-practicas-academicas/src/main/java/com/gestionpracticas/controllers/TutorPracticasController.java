package com.gestionpracticas.controllers;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.models.Usuario;
import com.gestionpracticas.repositories.UsuarioRepository;
import com.gestionpracticas.services.AlumnoService;
import com.gestionpracticas.services.EvaluacionService;
import com.gestionpracticas.services.TutorService;
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
@RequestMapping("/tutor-practicas")
@PreAuthorize("hasRole('TUTOR_PRACTICAS')")
@RequiredArgsConstructor
public class TutorPracticasController {

    private final TutorService tutorService;
    private final AlumnoService alumnoService;
    private final EvaluacionService evaluacionService;
    private final UsuarioRepository usuarioRepository;

    // ========================= DASHBOARD ========================= //

    /**
     * GET /tutor-practicas/dashboard
     * Dashboard principal del tutor de prácticas
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());
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
     * Lista de alumnos asignados al tutor
     */
    @GetMapping("/alumnos")
    public String alumnos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumnos", alumnos);

        return "tutor-practicas/alumnos";
    }

    /**
     * GET /tutor-practicas/alumnos/{id}
     * Detalle de un alumno específico
     */
    @GetMapping("/alumnos/{id}")
    public String alumnoDetalle(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());
        AlumnoDTO alumno = alumnoService.getAlumnoById(id);

        // Verificar que el alumno pertenece a este tutor
        if (!alumno.getTutorPracticasId().equals(tutor.getId())) {
            return "redirect:/tutor-practicas/alumnos?error=unauthorized";
        }

        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByAlumnoId(id);
        // TODO: Obtener observaciones e incidencias

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumno", alumno);
        model.addAttribute("evaluaciones", evaluaciones);

        return "tutor-practicas/alumno-detalle";
    }

    // ========================= EVALUACIONES ========================= //

    /**
     * GET /tutor-practicas/evaluaciones
     * Página de evaluaciones
     */
    @GetMapping("/evaluaciones")
    public String evaluaciones(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());
        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByTutorPracticasId(tutor.getId());

        model.addAttribute("tutor", tutor);
        model.addAttribute("evaluaciones", evaluaciones);

        return "tutor-practicas/evaluaciones";
    }

    /**
     * GET /tutor-practicas/evaluaciones/nueva
     * Formulario para crear nueva evaluación
     */
    @GetMapping("/evaluaciones/nueva")
    public String nuevaEvaluacion(@RequestParam(required = false) Long alumnoId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());
        List<CapacidadEvaluacionDTO> capacidades = evaluacionService.getAllCapacidadesEvaluacion();

        EvaluacionCreateDTO createDTO = new EvaluacionCreateDTO();
        if (alumnoId != null) {
            createDTO.setAlumnoId(alumnoId);
        }
        createDTO.setTutorPracticasId(tutor.getId());

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("capacidades", capacidades);
        model.addAttribute("evaluacionCreateDTO", createDTO);

        return "tutor-practicas/evaluacion-form";
    }

    /**
     * POST /tutor-practicas/evaluaciones
     * Crea una nueva evaluación
     */
    @PostMapping("/evaluaciones")
    public String createEvaluacion(@Valid @ModelAttribute EvaluacionCreateDTO createDTO,
                                   BindingResult result,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        if (result.hasErrors()) {
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());
            List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());
            List<CapacidadEvaluacionDTO> capacidades = evaluacionService.getAllCapacidadesEvaluacion();

            model.addAttribute("tutor", tutor);
            model.addAttribute("alumnos", alumnos);
            model.addAttribute("capacidades", capacidades);
            return "tutor-practicas/evaluacion-form";
        }

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());

        createDTO.setTutorPracticasId(tutor.getId());
        evaluacionService.createEvaluacion(createDTO);

        return "redirect:/tutor-practicas/evaluaciones?success=true";
    }

    // ========================= OBSERVACIONES DIARIAS ========================= //

    /**
     * GET /tutor-practicas/observaciones
     * Lista de observaciones diarias
     */
    @GetMapping("/observaciones")
    public String observaciones(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());
        
        // TODO: Implementar ObservacionDiariaService
        // Obtener observaciones de sus alumnos

        model.addAttribute("tutor", tutor);

        return "tutor-practicas/observaciones";
    }

    // ========================= INCIDENCIAS ========================= //

    /**
     * GET /tutor-practicas/incidencias
     * Lista de incidencias
     */
    @GetMapping("/incidencias")
    public String incidencias(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());

        // TODO: Implementar IncidenciaService
        // Obtener incidencias de sus alumnos

        model.addAttribute("tutor", tutor);

        return "tutor-practicas/incidencias";
    }

    /**
     * GET /tutor-practicas/incidencias/nueva
     * Formulario para crear nueva incidencia
     */
    @GetMapping("/incidencias/nueva")
    public String nuevaIncidencia(@RequestParam(required = false) Long alumnoId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());

        IncidenciaCreateDTO createDTO = new IncidenciaCreateDTO();
        if (alumnoId != null) {
            createDTO.setAlumnoId(alumnoId);
        }
        createDTO.setTutorPracticasId(tutor.getId());

        model.addAttribute("tutor", tutor);
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("incidenciaCreateDTO", createDTO);

        return "tutor-practicas/incidencia-form";
    }

    /**
     * POST /tutor-practicas/incidencias
     * Crea una nueva incidencia
     */
    @PostMapping("/incidencias")
    public String createIncidencia(@Valid @ModelAttribute IncidenciaCreateDTO createDTO,
                                   BindingResult result,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        if (result.hasErrors()) {
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());
            List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());

            model.addAttribute("tutor", tutor);
            model.addAttribute("alumnos", alumnos);
            return "tutor-practicas/incidencia-form";
        }

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());

        createDTO.setTutorPracticasId(tutor.getId());
        // TODO: Implementar IncidenciaService
        // incidenciaService.createIncidencia(createDTO);

        return "redirect:/tutor-practicas/incidencias?success=true";
    }

    // ========================= API REST ========================= //

    /**
     * GET /tutor-practicas/alumnos/api
     * Lista de alumnos (JSON)
     */
    @GetMapping("/alumnos/api")
    @ResponseBody
    public ResponseEntity<List<AlumnoDTO>> getAlumnosApi(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());
        List<AlumnoDTO> alumnos = alumnoService.getAlumnosByTutorPracticas(tutor.getId());

        return ResponseEntity.ok(alumnos);
    }

    /**
     * POST /tutor-practicas/evaluaciones/api
     * Crea una evaluación (JSON)
     */
    @PostMapping("/evaluaciones/api")
    @ResponseBody
    public ResponseEntity<EvaluacionDTO> createEvaluacionApi(@Valid @RequestBody EvaluacionCreateDTO createDTO,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TutorPracticasDTO tutor = tutorService.getTutorPracticasById(usuario.getReferenceId());
        createDTO.setTutorPracticasId(tutor.getId());

        EvaluacionDTO evaluacion = evaluacionService.createEvaluacion(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluacion);
    }
}