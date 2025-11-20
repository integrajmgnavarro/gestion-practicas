package com.gestionpracticas.controllers.admin;

import com.gestionpracticas.dto.CursoCreateDTO;
import com.gestionpracticas.dto.CursoDTO;
import com.gestionpracticas.dto.CursoUpdateDTO;
import com.gestionpracticas.dto.TutorCursoDTO;
import com.gestionpracticas.exception.BusinessException;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.services.CursoService;
import com.gestionpracticas.services.TutorCursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/cursos")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCursoController {

    private final CursoService cursoService;
    // Utilizamos el TutorCursoService para obtener la lista de tutores de curso para el selector
    private final TutorCursoService tutorCursoService; // <-- CAMBIADO: Inyectamos el servicio especializado

    /**
     * Muestra la lista de todos los cursos.
     * GET /admin/cursos
     */
    @GetMapping
    public String listarCursos(Model model) {
        try {
            List<CursoDTO> cursos = cursoService.getAllCursos();
            model.addAttribute("cursos", cursos);
            return "admin/cursos-list"; // Vista para listar cursos
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la lista de cursos: " + e.getMessage());
            return "admin/cursos-list";
        }
    }

    /**
     * Muestra el formulario para crear un nuevo curso.
     * GET /admin/cursos/nuevo
     */
    @GetMapping("/nuevo")
    public String nuevoCursoForm(Model model) {
        // Inicializa el DTO para el formulario de creación
        if (!model.containsAttribute("cursoCreateDTO")) {
            model.addAttribute("cursoCreateDTO", new CursoCreateDTO());
        }
        cargarTutoresCurso(model);
        return "admin/curso-form"; // Vista de creación
    }

    /**
     * Procesa la creación de un nuevo curso.
     * POST /admin/cursos
     */
    @PostMapping
    public String createCurso(@Valid @ModelAttribute("cursoCreateDTO") CursoCreateDTO createDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarTutoresCurso(model);
            return "admin/curso-form";
        }

        try {
            cursoService.createCurso(createDTO);
            redirectAttributes.addFlashAttribute("message", "Curso '" + createDTO.getNombre() + "' creado exitosamente.");
            return "redirect:/admin/cursos";
        } catch (DuplicateResourceException e) {
            // Error de Código duplicado
            result.rejectValue("codigo", "duplicate", e.getMessage());
            cargarTutoresCurso(model);
            return "admin/curso-form";
        } catch (ResourceNotFoundException e) {
            // Error si el Tutor de Curso no existe
            result.rejectValue("tutorCursoId", "notfound", "El ID de Tutor de Curso proporcionado no existe.");
            cargarTutoresCurso(model);
            return "admin/curso-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al crear el curso: " + e.getMessage());
            return "redirect:/admin/cursos";
        }
    }

    /**
     * Muestra el formulario para editar un curso.
     * GET /admin/cursos/{id}/editar
     */
    @GetMapping("/{id}/editar")
    public String editarCursoForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CursoDTO cursoDTO = cursoService.getCursoById(id);

            // Usamos un DTO de actualización para rellenar el formulario
            if (!model.containsAttribute("cursoUpdateDTO")) {
                CursoUpdateDTO updateDTO = mapToUpdateDTO(cursoDTO);
                model.addAttribute("cursoUpdateDTO", updateDTO);
            }
            
            cargarTutoresCurso(model);
            return "admin/curso-edit-form"; // Vista de edición
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/cursos";
        }
    }

    /**
     * Procesa la actualización de un curso existente.
     * POST /admin/cursos/{id}/editar
     */
    @PostMapping("/{id}/editar")
    public String updateCurso(@PathVariable Long id,
                              @Valid @ModelAttribute("cursoUpdateDTO") CursoUpdateDTO updateDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        updateDTO.setId(id); // Aseguramos que el ID de la URL prevalezca

        if (result.hasErrors()) {
            cargarTutoresCurso(model);
            return "admin/curso-edit-form";
        }

        try {
            cursoService.updateCurso(updateDTO);
            redirectAttributes.addFlashAttribute("message", "Curso actualizado exitosamente.");
            return "redirect:/admin/cursos";
        } catch (DuplicateResourceException e) {
            // Error de Código duplicado
            result.rejectValue("codigo", "duplicate", e.getMessage());
            cargarTutoresCurso(model);
            return "admin/curso-edit-form";
        } catch (ResourceNotFoundException e) {
            // Error si el curso o el Tutor de Curso no existe
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Tutor")) {
                 result.rejectValue("tutorCursoId", "notfound", "El ID de Tutor de Curso proporcionado no existe.");
            } else {
                redirectAttributes.addFlashAttribute("error", errorMessage);
                return "redirect:/admin/cursos";
            }
            cargarTutoresCurso(model);
            return "admin/curso-edit-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al actualizar el curso: " + e.getMessage());
            return "redirect:/admin/cursos";
        }
    }

    /**
     * Elimina un curso.
     * POST /admin/cursos/{id}/eliminar
     */
    @PostMapping("/{id}/eliminar")
    public String deleteCurso(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cursoService.deleteCurso(id);
            redirectAttributes.addFlashAttribute("message", "Curso eliminado exitosamente.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (BusinessException e) {
            // Captura el error si el curso tiene alumnos asociados
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al eliminar el curso.");
        }
        return "redirect:/admin/cursos";
    }

    // ========================= MÉTODOS PRIVADOS DE UTILIDAD ========================= //

    /**
     * Carga la lista de tutores de curso para el selector del formulario.
     */
    private void cargarTutoresCurso(Model model) {
        try {
            // Ahora utilizamos el servicio especializado TutorCursoService
            List<TutorCursoDTO> tutoresCurso = tutorCursoService.findAllList(); 
            model.addAttribute("tutoresCurso", tutoresCurso);
        } catch (Exception e) {
            model.addAttribute("errorTutores", "No se pudo cargar la lista de Tutores de Curso. Asegúrese de que TutorCursoService está operativo.");
        }
    }
    
    /**
     * Mapea el DTO de lectura (CursoDTO) al DTO de actualización (CursoUpdateDTO) 
     * para rellenar el formulario de edición.
     */
    private CursoUpdateDTO mapToUpdateDTO(CursoDTO cursoDTO) {
        CursoUpdateDTO updateDTO = new CursoUpdateDTO();
        updateDTO.setId(cursoDTO.getId());
        updateDTO.setNombre(cursoDTO.getNombre());
        updateDTO.setCodigo(cursoDTO.getCodigo());
        updateDTO.setDescripcion(cursoDTO.getDescripcion());
        updateDTO.setDuracion(cursoDTO.getDuracion());
        updateDTO.setFechaInicio(cursoDTO.getFechaInicio());
        updateDTO.setFechaFin(cursoDTO.getFechaFin());
        updateDTO.setActivo(cursoDTO.getActivo());
        updateDTO.setTutorCursoId(cursoDTO.getTutorCursoId());
        return updateDTO;
    }
}
