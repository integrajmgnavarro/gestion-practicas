package com.gestionpracticas.controllers.alumno;

import com.gestionpracticas.dto.AlumnoDTO;
import com.gestionpracticas.dto.AlumnoUpdateDTO;
import com.gestionpracticas.dto.EvaluacionDTO;
import com.gestionpracticas.dto.ObservacionDiariaCreateDTO;
import com.gestionpracticas.dto.ObservacionDiariaDTO; // Nueva Importación para el DTO de respuesta
import com.gestionpracticas.exception.ResourceNotFoundException; // Para un manejo de errores más específico
import com.gestionpracticas.models.Usuario;
import com.gestionpracticas.repositories.UsuarioRepository;
import com.gestionpracticas.services.AlumnoService;
import com.gestionpracticas.services.EvaluacionService;
import com.gestionpracticas.services.ObservacionDiariaService; // Descomentado y utilizado
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AlumnoApiController
 * Controla todas las peticiones REST (JSON) para funcionalidades específicas del rol ALUMNO.
 */
@RestController
@RequestMapping("/alumno/api")
@PreAuthorize("hasRole('ALUMNO')")
@RequiredArgsConstructor
public class AlumnoApiController {

    private final AlumnoService alumnoService;
    private final EvaluacionService evaluacionService;
    private final UsuarioRepository usuarioRepository;
    private final ObservacionDiariaService observacionDiariaService; // Ahora utilizado

    // ========================= API REST ========================= //

    /**
     * Helper para obtener el ID del alumno a partir del usuario autenticado
     */
    private Long getAlumnoIdFromUserDetails(UserDetails userDetails) {
        // En un entorno de producción, es preferible que este método se encuentre
        // en un servicio de utilidad o en el propio AlumnoService.
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado o no autorizado."));
        return usuario.getReferenceId(); // Usar reference_id para obtener el ID del Alumno
    }

    /**
     * GET /alumno/api/perfil
     * Obtiene los datos del perfil del alumno (JSON)
     */
    @GetMapping("/perfil")
    public ResponseEntity<AlumnoDTO> getPerfilApi(@AuthenticationPrincipal UserDetails userDetails) {
        Long alumnoId = getAlumnoIdFromUserDetails(userDetails);
        // Asumiendo que getAlumnoById lanza ResourceNotFoundException si no existe,
        // Spring lo mapeará a 404.
        AlumnoDTO alumno = alumnoService.getAlumnoById(alumnoId); 
        return ResponseEntity.ok(alumno);
    }

    /**
     * PUT /alumno/api/perfil
     * Actualiza el perfil del alumno (JSON). SOLO debe permitir actualizar campos
     * que son modificables por el propio alumno (Ej: teléfono, dirección, etc.).
     */
    @PutMapping("/perfil")
    public ResponseEntity<AlumnoDTO> updatePerfilApi(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AlumnoUpdateDTO updateDTO) {

        Long alumnoId = getAlumnoIdFromUserDetails(userDetails);

        // Seguridad: Se sobreescribe el ID del DTO con el ID del usuario logueado.
        // Esto previene que un alumno intente actualizar el perfil de otro alumno
        // enviando un ID diferente en el cuerpo de la petición.
        updateDTO.setId(alumnoId); 
        
        AlumnoDTO updated = alumnoService.updateAlumno(updateDTO);

        return ResponseEntity.ok(updated);
    }

    /**
     * GET /alumno/api/evaluaciones
     * Obtiene las evaluaciones del alumno (JSON)
     */
    @GetMapping("/evaluaciones")
    public ResponseEntity<List<EvaluacionDTO>> getEvaluacionesApi(@AuthenticationPrincipal UserDetails userDetails) {
        Long alumnoId = getAlumnoIdFromUserDetails(userDetails);
        
        List<EvaluacionDTO> evaluaciones = evaluacionService.getEvaluacionesByAlumnoId(alumnoId);

        return ResponseEntity.ok(evaluaciones);
    }

    /**
     * GET /alumno/api/observaciones
     * Obtiene las observaciones diarias del alumno (JSON)
     */
    @GetMapping("/observaciones")
    public ResponseEntity<List<ObservacionDiariaDTO>> getObservacionesApi(@AuthenticationPrincipal UserDetails userDetails) {
        Long alumnoId = getAlumnoIdFromUserDetails(userDetails);

        // TODO Resuelto: Implementado ObservacionDiariaService.getObservacionesByAlumnoId
        List<ObservacionDiariaDTO> observaciones = observacionDiariaService.getObservacionesByAlumnoId(alumnoId);
        
        return ResponseEntity.ok(observaciones);
    }

    /**
     * POST /alumno/api/observaciones
     * Crea una nueva observación diaria (JSON). El alumnoId se obtiene del usuario autenticado.
     */
    @PostMapping("/observaciones")
    public ResponseEntity<ObservacionDiariaDTO> createObservacionApi(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ObservacionDiariaCreateDTO createDTO) {

        Long alumnoId = getAlumnoIdFromUserDetails(userDetails);
        
        // Seguridad: Asignamos el ID del alumno logueado al DTO, ignorando cualquier valor
        // que pudiera venir en el cuerpo de la petición.
        createDTO.setAlumnoId(alumnoId); 

        // TODO Resuelto: Implementado ObservacionDiariaService.createObservacion
        ObservacionDiariaDTO observacion = observacionDiariaService.createObservacion(createDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(observacion);
    }
}
