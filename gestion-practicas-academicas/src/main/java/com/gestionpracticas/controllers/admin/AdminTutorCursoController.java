package com.gestionpracticas.controllers.admin;

import com.gestionpracticas.dto.TutorCursoCreateDTO;
import com.gestionpracticas.dto.TutorCursoDTO;
import com.gestionpracticas.dto.TutorCursoUpdateDTO;
import com.gestionpracticas.exception.BusinessException;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
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
@RequestMapping("/admin/tutores-curso")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminTutorCursoController {

    private final TutorCursoService tutorCursoService;

    /**
     * Muestra la lista de todos los tutores de curso.
     * GET /admin/tutores-curso
     */
    @GetMapping
    public String listarTutores(Model model) {
        try {
            List<TutorCursoDTO> tutores = tutorCursoService.getAllTutoresCurso();
            model.addAttribute("tutores", tutores);
            return "admin/tutores-curso-list"; // Vista: admin/tutores-curso-list.html
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la lista de tutores de curso.");
            return "admin/tutores-curso-list";
        }
    }

    /**
     * Muestra el formulario para crear un nuevo tutor de curso.
     * GET /admin/tutores-curso/nuevo
     */
    @GetMapping("/nuevo")
    public String nuevoTutorForm(Model model) {
        model.addAttribute("tutorCreateDTO", new TutorCursoCreateDTO());
        return "admin/tutor-curso-form"; // Vista: admin/tutor-curso-form.html
    }

    /**
     * Procesa la creación de un nuevo tutor de curso.
     * POST /admin/tutores-curso
     */
    @PostMapping
    public String createTutor(@Valid @ModelAttribute("tutorCreateDTO") TutorCursoCreateDTO createDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/tutor-curso-form";
        }

        try {
            tutorCursoService.createTutorCurso(createDTO);
            redirectAttributes.addFlashAttribute("message", "Tutor '" + createDTO.getNombre() + " " + createDTO.getApellidos() + "' creado exitosamente.");
            return "redirect:/admin/tutores-curso";
        } catch (DuplicateResourceException e) {
            // Error de DNI o Email duplicado
            manejarErroresUnicidad(e, result);
            return "admin/tutor-curso-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al crear el tutor.");
            return "redirect:/admin/tutores-curso";
        }
    }

    /**
     * Muestra el formulario para editar un tutor de curso.
     * GET /admin/tutores-curso/{id}/editar
     */
    @GetMapping("/{id}/editar")
    public String editarTutorForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TutorCursoDTO tutorDTO = tutorCursoService.getTutorCursoById(id);

            // Mapeamos TutorCursoDTO a TutorCursoUpdateDTO
            TutorCursoUpdateDTO updateDTO = new TutorCursoUpdateDTO();
            updateDTO.setId(tutorDTO.getId());
            updateDTO.setNombre(tutorDTO.getNombre());
            updateDTO.setApellidos(tutorDTO.getApellidos());
            updateDTO.setDni(tutorDTO.getDni());
            updateDTO.setEmail(tutorDTO.getEmail());
            updateDTO.setTelefono(tutorDTO.getTelefono());
            updateDTO.setDepartamento(tutorDTO.getDepartamento());
            updateDTO.setActivo(tutorDTO.getActivo());

            model.addAttribute("tutorUpdateDTO", updateDTO);
            return "admin/tutor-curso-edit-form"; // Vista: admin/tutor-curso-edit-form.html
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/tutores-curso";
        }
    }

    /**
     * Procesa la actualización de un tutor existente.
     * POST /admin/tutores-curso/{id}/editar
     */
    @PostMapping("/{id}/editar")
    public String updateTutor(@PathVariable Long id,
                              @Valid @ModelAttribute("tutorUpdateDTO") TutorCursoUpdateDTO updateDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {

        updateDTO.setId(id); // Aseguramos que el ID de la URL sea el que se use

        if (result.hasErrors()) {
            return "admin/tutor-curso-edit-form";
        }

        try {
            tutorCursoService.updateTutorCurso(id, updateDTO);
            redirectAttributes.addFlashAttribute("message", "Tutor de Curso actualizado exitosamente.");
            return "redirect:/admin/tutores-curso";
        } catch (DuplicateResourceException e) {
            manejarErroresUnicidad(e, result);
            return "admin/tutor-curso-edit-form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/tutores-curso";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al actualizar el tutor.");
            return "redirect:/admin/tutores-curso";
        }
    }

    /**
     * Elimina un tutor.
     * POST /admin/tutores-curso/{id}/eliminar
     */
    @PostMapping("/{id}/eliminar")
    public String deleteTutor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tutorCursoService.deleteTutorCurso(id);
            redirectAttributes.addFlashAttribute("message", "Tutor de Curso eliminado exitosamente.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (BusinessException e) {
            // Captura el error de integridad (alumnos o cursos asignados)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al eliminar el tutor.");
        }
        return "redirect:/admin/tutores-curso";
    }

    // ========================= MÉTODOS PRIVADOS DE UTILIDAD ========================= //

    /**
     * Maneja errores de DNI/Email duplicado para mostrarlos en el campo correcto.
     */
    private void manejarErroresUnicidad(DuplicateResourceException e, BindingResult result) {
        String message = e.getMessage();
        if (message.contains("DNI")) {
            result.rejectValue("dni", "duplicate", message);
        } else if (message.contains("Email")) {
            result.rejectValue("email", "duplicate", message);
        } else {
             result.reject("globalError", message);
        }
    }
}
