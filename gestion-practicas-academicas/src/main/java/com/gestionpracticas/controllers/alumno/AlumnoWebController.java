package com.gestionpracticas.controllers.alumno;

import com.gestionpracticas.dto.AlumnoDTO;
import com.gestionpracticas.dto.AlumnoUpdateDTO;
import com.gestionpracticas.dto.EvaluacionDTO;
import com.gestionpracticas.dto.ObservacionDiariaCreateDTO;
import com.gestionpracticas.dto.ObservacionDiariaDTO;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.models.Usuario;
import com.gestionpracticas.repositories.UsuarioRepository;
import com.gestionpracticas.services.AlumnoService;
import com.gestionpracticas.services.EvaluacionService;
import com.gestionpracticas.services.ObservacionDiariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AlumnoWebController
 * Controla todas las peticiones que devuelven vistas (HTML) para el rol ALUMNO.
 */
@Controller
@RequestMapping("/alumno")
@PreAuthorize("hasRole('ALUMNO')")
@RequiredArgsConstructor
public class AlumnoWebController {

    private final AlumnoService alumnoService;
    private final EvaluacionService evaluacionService;
    private final UsuarioRepository usuarioRepository;
    private final ObservacionDiariaService observacionDiariaService;

    // ========================= UTILITY ========================= //
    /**
     * Helper para obtener el ID del alumno a partir del usuario autenticado,
     * centralizando la lógica de seguridad y el manejo de ResourceNotFoundException.
     */
    private Long getAlumnoIdFromUserDetails(UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado o no autorizado."));
        return usuario.getReferenceId(); // Usar reference_id para obtener el ID del Alumno
    }

    // ========================= VISTAS ========================= //

    /**
     * GET /alumno/dashboard
     * Muestra la página de inicio o dashboard del alumno.
     */
    @GetMapping("/dashboard")
    public String showAlumnoDashboard(Model model) {
        return "alumno/dashboard";
    }

    /**
     * GET /alumno/perfil
     * Muestra la página de perfil del alumno.
     */
    @GetMapping("/perfil")
    public String perfilPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        Long alumnoId = getAlumnoIdFromUserDetails(userDetails);

        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);

        // Se obtienen los datos estadísticos del perfil
        Double notaMedia = evaluacionService.calcularNotaMedia(alumnoId);
        Long totalEvaluaciones = evaluacionService.contarEvaluaciones(alumnoId);
        Long totalObservaciones = observacionDiariaService.contarObservaciones(alumnoId);


        model.addAttribute("alumno", alumno);
        model.addAttribute("notaMedia", notaMedia);
        model.addAttribute("totalEvaluaciones", totalEvaluaciones);
        model.addAttribute("totalObservaciones", totalObservaciones);

        // Inicializar el DTO para el modal o formulario de actualización, precargando campos
        AlumnoUpdateDTO updateDTO = new AlumnoUpdateDTO();
        updateDTO.setTelefono(alumno.getTelefono());
        // Se puede inicializar más campos aquí si son editables (ej. dirección)
        model.addAttribute("alumnoUpdateDTO", updateDTO);

        return "alumno/perfil";
    }


    /**
     * POST /alumno/perfil
     * Procesa la actualización del perfil (formulario).
     */
    @PostMapping("/perfil")
    public String updatePerfil(@AuthenticationPrincipal UserDetails userDetails,
                               @Valid @ModelAttribute AlumnoUpdateDTO updateDTO,
                               BindingResult result,
                               Model model) {

        Long alumnoId = getAlumnoIdFromUserDetails(userDetails);

        if (result.hasErrors()) {
            // Recargar todos los datos necesarios para mostrar la vista en caso de error de validación
            AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);
            
            Double notaMedia = evaluacionService.calcularNotaMedia(alumnoId);
            Long totalEvaluaciones = evaluacionService.contarEvaluaciones(alumnoId);
            Long totalObservaciones = observacionDiariaService.contarObservaciones(alumnoId);

            model.addAttribute("alumno", alumno);
            model.addAttribute("notaMedia", notaMedia);
            model.addAttribute("totalEvaluaciones", totalEvaluaciones);
            model.addAttribute("totalObservaciones", totalObservaciones);

            // Asegurarse de que el DTO con errores y los datos estadísticos estén en el modelo
            return "alumno/perfil";
        }

        // Seguridad: Se sobreescribe el ID del DTO con el ID del usuario logueado.
        updateDTO.setId(alumnoId);
        alumnoService.updateAlumno(updateDTO);

        return "redirect:/alumno/perfil?success=true";
    }

    /**
     * GET /alumno/evaluaciones
     * Muestra la lista de evaluaciones del alumno.
     */
    @GetMapping("/evaluaciones")
    public String evaluacionesPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long alumnoId = getAlumnoIdFromUserDetails(userDetails);

        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);
        
        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByAlumnoId(alumnoId);

        model.addAttribute("alumno", alumno);
        model.addAttribute("evaluaciones", evaluaciones);

        return "alumno/evaluaciones";
    }

    /**
     * GET /alumno/observaciones
     * Muestra la página de observaciones diarias del alumno.
     */
    @GetMapping("/observaciones")
    public String observacionesPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long alumnoId = getAlumnoIdFromUserDetails(userDetails);

        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);

        // TODO Resuelto: Implementar ObservacionDiariaService y obtener las observaciones
        List<ObservacionDiariaDTO> observaciones = observacionDiariaService.getObservacionesByAlumnoId(alumnoId);
        model.addAttribute("observaciones", observaciones);

        model.addAttribute("alumno", alumno);
        // Se añade el DTO vacío para el formulario de creación en la vista
        model.addAttribute("observacionCreateDTO", new ObservacionDiariaCreateDTO());

        return "alumno/observaciones";
    }

    /**
     * POST /alumno/observaciones
     * Crea una nueva observación diaria (formulario)
     */
    @PostMapping("/observaciones")
    public String createObservacion(@AuthenticationPrincipal UserDetails userDetails,
                                     @Valid @ModelAttribute ObservacionDiariaCreateDTO createDTO,
                                     BindingResult result,
                                     Model model) {

        Long alumnoId = getAlumnoIdFromUserDetails(userDetails);
        
        if (result.hasErrors()) {
            // Recargar datos necesarios para mostrar la vista con errores
            model.addAttribute("alumno", alumnoService.getAlumnoById(alumnoId));
            // Recargar las observaciones existentes para mantener el contexto
            List<ObservacionDiariaDTO> observaciones = observacionDiariaService.getObservacionesByAlumnoId(alumnoId);
            model.addAttribute("observaciones", observaciones);

            return "alumno/observaciones";
        }

        // Seguridad: Asignamos el ID del alumno logueado al DTO.
        createDTO.setAlumnoId(alumnoId);

        // TODO Resuelto: Implementar ObservacionDiariaService
        observacionDiariaService.createObservacion(createDTO);

        return "redirect:/alumno/observaciones?success=true";
    }
}
