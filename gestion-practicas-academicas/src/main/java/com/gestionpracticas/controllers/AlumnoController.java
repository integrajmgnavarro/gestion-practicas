package com.gestionpracticas.controllers;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.services.AlumnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AlumnoController {

    private final AlumnoService alumnoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlumnoDTO> createAlumno(@Valid @RequestBody AlumnoCreateDTO createDTO) {
        AlumnoDTO alumno = alumnoService.createAlumno(createDTO);
        return new ResponseEntity<>(alumno, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS', 'ALUMNO')")
    public ResponseEntity<AlumnoDTO> getAlumnoById(@PathVariable Long id) {
        return ResponseEntity.ok(alumnoService.getAlumnoById(id));
    }

    @GetMapping("/dni/{dni}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<AlumnoDTO> getAlumnoByDni(@PathVariable String dni) {
        return ResponseEntity.ok(alumnoService.getAlumnoByDni(dni));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO')")
    public ResponseEntity<AlumnoDTO> getAlumnoByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(alumnoService.getAlumnoByUsuarioId(usuarioId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlumnoDTO>> getAllAlumnos() {
        return ResponseEntity.ok(alumnoService.getAllAlumnos());
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<List<AlumnoDTO>> getAlumnosActivos() {
        return ResponseEntity.ok(alumnoService.getAlumnosActivos());
    }

    @GetMapping("/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<List<AlumnoDTO>> getAlumnosByCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(alumnoService.getAlumnosByCurso(cursoId));
    }

    @GetMapping("/empresa/{empresaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<List<AlumnoDTO>> getAlumnosByEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(alumnoService.getAlumnosByEmpresa(empresaId));
    }

    @GetMapping("/tutor-practicas/{tutorPracticasId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<List<AlumnoDTO>> getAlumnosByTutorPracticas(@PathVariable Long tutorPracticasId) {
        return ResponseEntity.ok(alumnoService.getAlumnosByTutorPracticas(tutorPracticasId));
    }

    @GetMapping("/tutor-curso/{tutorCursoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<List<AlumnoDTO>> getAlumnosByTutorCurso(@PathVariable Long tutorCursoId) {
        return ResponseEntity.ok(alumnoService.getAlumnosByTutorCurso(tutorCursoId));
    }

    @GetMapping("/practicas-activas")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<List<AlumnoDTO>> getAlumnosConPracticasActivas() {
        return ResponseEntity.ok(alumnoService.getAlumnosConPracticasActivas());
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<List<AlumnoDTO>> searchAlumnos(@RequestBody AlumnoSearchDTO searchDTO) {
        return ResponseEntity.ok(alumnoService.searchAlumnos(searchDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlumnoDTO> updateAlumno(@PathVariable Long id, @Valid @RequestBody AlumnoUpdateDTO updateDTO) {
        return ResponseEntity.ok(alumnoService.updateAlumno(id, updateDTO));
    }

    @PatchMapping("/{id}/perfil")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<AlumnoDTO> updatePerfilAlumno(@PathVariable Long id, @RequestBody AlumnoUpdateDTO updateDTO) {
        AlumnoUpdateDTO limited = new AlumnoUpdateDTO();
        limited.setTelefono(updateDTO.getTelefono());
        return ResponseEntity.ok(alumnoService.updateAlumno(id, limited));
    }

    @PutMapping("/{alumnoId}/asignar-empresa")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlumnoDTO> asignarEmpresaYTutor(@PathVariable Long alumnoId, @RequestBody Map<String, Long> asignacion) {
        Long empresaId = asignacion.get("empresaId");
        Long tutorPracticasId = asignacion.get("tutorPracticasId");
        return ResponseEntity.ok(alumnoService.asignarEmpresaYTutor(alumnoId, empresaId, tutorPracticasId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteAlumno(@PathVariable Long id) {
        alumnoService.deleteAlumno(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Alumno eliminado correctamente");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlumnoDTO> desactivarAlumno(@PathVariable Long id) {
        AlumnoUpdateDTO dto = new AlumnoUpdateDTO();
        dto.setActivo(false);
        return ResponseEntity.ok(alumnoService.updateAlumno(id, dto));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlumnoDTO> activarAlumno(@PathVariable Long id) {
        AlumnoUpdateDTO dto = new AlumnoUpdateDTO();
        dto.setActivo(true);
        return ResponseEntity.ok(alumnoService.updateAlumno(id, dto));
    }
}
