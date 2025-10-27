package com.gestionpracticas.controllers;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.models.Usuario;
import com.gestionpracticas.repositories.UsuarioRepository;
import com.gestionpracticas.services.AlumnoService;
import com.gestionpracticas.services.EvaluacionService;
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
@RequestMapping("/alumno")
@PreAuthorize("hasRole('ALUMNO')")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;
    private final EvaluacionService evaluacionService;
    private final UsuarioRepository usuarioRepository;

    // Asunción: Necesitas una instancia de ObservacionDiariaService para los métodos TO-DO
    // private final ObservacionDiariaService observacionDiariaService;

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
     * Muestra la página de perfil del alumno
     * CLAVE: Se obtiene el ID del Alumno a través del reference_id del Usuario.
     */
    @GetMapping("/perfil")
    public String perfilPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        // 1. Obtener el objeto Usuario completo
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. OBTENER EL ID DEL ALUMNO USANDO EL REFERENCE_ID
        // Esta es la corrección crucial
        Long alumnoId = usuario.getReferenceId();

        // 3. Obtener el Alumno DTO y datos estadísticos para la vista
        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId); // Asunción: getAlumnoById usa el PK de Alumno

        Double notaMedia = alumnoService.calcularNotaMedia(alumnoId);
        Long totalEvaluaciones = alumnoService.contarEvaluaciones(alumnoId);
        Long totalObservaciones = alumnoService.contarObservaciones(alumnoId);

        // 4. Añadir al modelo
        model.addAttribute("alumno", alumno);
        model.addAttribute("notaMedia", notaMedia);
        model.addAttribute("totalEvaluaciones", totalEvaluaciones);
        model.addAttribute("totalObservaciones", totalObservaciones);


        // Inicializar el DTO para el modal, usando el teléfono actual del alumno
        // CORRECCIÓN: Usar constructor sin argumentos y setter, ya que no existe un constructor AlumnoUpdateDTO(String)
        AlumnoUpdateDTO updateDTO = new AlumnoUpdateDTO();
        updateDTO.setTelefono(alumno.getTelefono()); // Pre-rellenar el campo de teléfono
        model.addAttribute("alumnoUpdateDTO", updateDTO);

        return "alumno/perfil";
    }


    /**
     * POST /alumno/perfil
     * Procesa la actualización del perfil (formulario)
     */
    @PostMapping("/perfil")
    public String updatePerfil(@AuthenticationPrincipal UserDetails userDetails,
                                 @Valid @ModelAttribute AlumnoUpdateDTO updateDTO,
                                 BindingResult result,
                                 Model model) {

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long alumnoId = usuario.getReferenceId(); // Usar reference_id

        if (result.hasErrors()) {
            // Recargar todos los datos necesarios en caso de error de validación
            AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);
            Double notaMedia = alumnoService.calcularNotaMedia(alumnoId);
            Long totalEvaluaciones = alumnoService.contarEvaluaciones(alumnoId);
            Long totalObservaciones = alumnoService.contarObservaciones(alumnoId);

            model.addAttribute("alumno", alumno);
            model.addAttribute("notaMedia", notaMedia);
            model.addAttribute("totalEvaluaciones", totalEvaluaciones);
            model.addAttribute("totalObservaciones", totalObservaciones);

            return "alumno/perfil";
        }

        // AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId); // No es necesario obtenerlo solo para el ID
        alumnoService.updateAlumno(alumnoId, updateDTO);

        // model.addAttribute("message", "Perfil actualizado correctamente"); // Usar flash attributes para redirecciones
        return "redirect:/alumno/perfil?success=true";
    }

    /**
     * GET /alumno/evaluaciones
     * Muestra las evaluaciones del alumno
     */
    @GetMapping("/evaluaciones")
    public String evaluacionesPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long alumnoId = usuario.getReferenceId(); // Usar reference_id

        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);
        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByAlumnoId(alumno.getId());

        model.addAttribute("alumno", alumno);
        model.addAttribute("evaluaciones", evaluaciones);

        return "alumno/evaluaciones";
    }

    /**
     * GET /alumno/observaciones
     * Muestra las observaciones del alumno
     */
    @GetMapping("/observaciones")
    public String observacionesPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long alumnoId = usuario.getReferenceId(); // Usar reference_id

        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);

        // TODO: Implementar ObservacionDiariaService y obtener las observaciones
        // List<ObservacionDiariaDTO> observaciones = observacionDiariaService.getObservacionesByAlumnoId(alumno.getId());
        // model.addAttribute("observaciones", observaciones);

        model.addAttribute("alumno", alumno);
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
        if (result.hasErrors()) {
            // Se asume que necesitas recargar el objeto 'alumno' para la vista en caso de error.
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Long alumnoId = usuario.getReferenceId();
            model.addAttribute("alumno", alumnoService.getAlumnoById(alumnoId));

            return "alumno/observaciones";
        }

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long alumnoId = usuario.getReferenceId(); // Usar reference_id

        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);
        createDTO.setAlumnoId(alumno.getId());

        // TODO: Implementar ObservacionDiariaService
        // observacionDiariaService.createObservacion(createDTO);

        return "redirect:/alumno/observaciones?success=true";
    }

    // ========================= API REST ========================= //

    /**
     * GET /alumno/perfil/api
     * Obtiene los datos del perfil del alumno (JSON)
     */
    @GetMapping("/perfil/api")
    @ResponseBody
    public ResponseEntity<AlumnoDTO> getPerfilApi(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long alumnoId = usuario.getReferenceId(); // Usar reference_id

        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);
        return ResponseEntity.ok(alumno);
    }

    /**
     * PUT /alumno/perfil/api
     * Actualiza el perfil del alumno (JSON)
     */
    @PutMapping("/perfil/api")
    @ResponseBody
    public ResponseEntity<AlumnoDTO> updatePerfilApi(@AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody AlumnoUpdateDTO updateDTO) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long alumnoId = usuario.getReferenceId(); // Usar reference_id

        // AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId); // No es necesario obtenerlo
        AlumnoDTO updated = alumnoService.updateAlumno(alumnoId, updateDTO);

        return ResponseEntity.ok(updated);
    }

    /**
     * GET /alumno/evaluaciones/api
     * Obtiene las evaluaciones del alumno (JSON)
     */
    @GetMapping("/evaluaciones/api")
    @ResponseBody
    public ResponseEntity<List<EvaluacionDTO>> getEvaluacionesApi(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long alumnoId = usuario.getReferenceId(); // Usar reference_id

        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);
        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByAlumnoId(alumno.getId());

        return ResponseEntity.ok(evaluaciones);
    }

    /**
     * GET /alumno/observaciones/api
     * Obtiene las observaciones del alumno (JSON)
     */
    @GetMapping("/observaciones/api")
    @ResponseBody
    public ResponseEntity<?> getObservacionesApi(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long alumnoId = usuario.getReferenceId(); // Usar reference_id

        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);

        // TODO: Implementar ObservacionDiariaService
        // List<ObservacionDiariaDTO> observaciones = observacionDiariaService.getObservacionesByAlumnoId(alumno.getId());
        // return ResponseEntity.ok(observaciones);

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Servicio no implementado aún");
    }

    /**
     * POST /alumno/observaciones/api
     * Crea una nueva observación diaria (JSON)
     */
    @PostMapping("/observaciones/api")
    @ResponseBody
    public ResponseEntity<?> createObservacionApi(@AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody ObservacionDiariaCreateDTO createDTO) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long alumnoId = usuario.getReferenceId(); // Usar reference_id

        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId);
        createDTO.setAlumnoId(alumno.getId());

        // TODO: Implementar ObservacionDiariaService
        // ObservacionDiariaDTO observacion = observacionDiariaService.createObservacion(createDTO);
        // return ResponseEntity.status(HttpStatus.CREATED).body(observacion);

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Servicio no implementado aún");
    }
}
