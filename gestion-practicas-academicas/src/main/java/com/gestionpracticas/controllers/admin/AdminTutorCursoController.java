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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tutores-curso")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminTutorCursoController {

    private final TutorCursoService tutorCursoService;

    // ========================= GESTIÓN DE LISTADO Y BÚSQUEDA ========================= //

    /**
     * Muestra la lista de todos los tutores de curso con paginación y filtros.
     */
    @GetMapping
    public String listTutors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "apellidos,asc") String sort,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) Boolean activo,
            Model model) {

        String[] sortParams = sort.split(",");
        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<TutorCursoDTO> tutoresPage = tutorCursoService.findAll(
            nombre, 
            apellidos, 
            dni, 
            especialidad, 
            activo, 
            pageable
        ); 

        model.addAttribute("tutoresPage", tutoresPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortParams[0]);
        model.addAttribute("sortDirection", sortParams[1]);
        model.addAttribute("nombre", nombre);
        model.addAttribute("apellidos", apellidos);
        model.addAttribute("dni", dni);
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("activo", activo);
        
        return "admin/tutor-curso";
    }

    // ========================= CREACIÓN ========================= //

    /**
     * Muestra el formulario de creación.
     */
    @GetMapping("/nuevo")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("tutorCursoCreateDTO")) {
            model.addAttribute("tutorCursoCreateDTO", new TutorCursoCreateDTO());
        }
        return "admin/tutor-curso-form";
    }

    /**
     * Procesa la creación de un nuevo tutor.
     */
    @PostMapping("/nuevo")
    public String createTutor(@Valid TutorCursoCreateDTO dto, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tutorCursoCreateDTO", result);
            redirectAttributes.addFlashAttribute("tutorCursoCreateDTO", dto);
            return "redirect:/admin/tutores-curso/nuevo";
        }

        try {
            tutorCursoService.createTutorCurso(dto);
            redirectAttributes.addFlashAttribute("message", "Tutor de Curso creado exitosamente.");
            return "redirect:/admin/tutores-curso";
        } catch (DuplicateResourceException e) {
            manejarErroresUnicidad(e, result);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tutorCursoCreateDTO", result);
            redirectAttributes.addFlashAttribute("tutorCursoCreateDTO", dto);
            return "redirect:/admin/tutores-curso/nuevo";
        }
    }

    // ========================= EDICIÓN (CORREGIDA) ========================= //

    /**
     * Muestra el formulario de edición.
     */
    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("tutorCursoUpdateDTO")) {
            try {
                TutorCursoDTO tutorDTO = tutorCursoService.findById(id);
                // Simplificamos la lógica y añadimos el DTO correcto
                TutorCursoUpdateDTO updateDTO = mapToUpdateDTO(tutorDTO);
                model.addAttribute("tutorCursoUpdateDTO", updateDTO);
                
            } catch (ResourceNotFoundException e) {
                return "redirect:/admin/tutores-curso";
            }
        }
        
        // 💥 CORRECCIÓN FINAL EN JAVA: Aseguramos que el ID esté en el modelo para el th:action
        model.addAttribute("tutorId", id);
        
        return "admin/tutor-curso-edit-form";
    }

    /**
     * Procesa la actualización de un tutor.
     */
    @PostMapping("/{id}/editar")
    public String updateTutor(
            @PathVariable Long id, 
            @Valid TutorCursoUpdateDTO dto, 
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Si hay errores de validación, volvemos al formulario.
        if (result.hasErrors()) {
            model.addAttribute("tutorCursoUpdateDTO", dto);
            // 💥 CORRECCIÓN FINAL EN JAVA: Preservar el ID en caso de error
            model.addAttribute("tutorId", id); 
            return "admin/tutor-curso-edit-form";
        }

        try {
            // Asignamos el ID del PathVariable al DTO
            dto.setId(id);
            
            // Llama al servicio SOLO con el DTO
            tutorCursoService.updateTutorCurso(dto); 
            
            redirectAttributes.addFlashAttribute("message", "Tutor de Curso actualizado exitosamente.");
            return "redirect:/admin/tutores-curso";
        } catch (DuplicateResourceException e) {
            manejarErroresUnicidad(e, result);
            model.addAttribute("tutorCursoUpdateDTO", dto); // Volver a pasar el DTO con errores
            // 💥 CORRECCIÓN FINAL EN JAVA: Preservar el ID en caso de error
            model.addAttribute("tutorId", id); 
            return "admin/tutor-curso-edit-form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/tutores-curso";
        }
    }


    // ========================= ELIMINACIÓN ========================= //

    /**
     * Procesa la eliminación de un tutor.
     */
    @PostMapping("/{id}/eliminar")
    public String deleteTutor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tutorCursoService.deleteTutorCurso(id);
            redirectAttributes.addFlashAttribute("message", "Tutor de Curso eliminado exitosamente.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (BusinessException e) {
            // Atrapa errores de negocio específicos (ej. si el tutor tiene cursos o alumnos asignados)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al eliminar el tutor: " + e.getMessage());
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
        } else if (message.contains("email")) {
            result.rejectValue("email", "duplicate", message);
        } else {
             result.reject("globalError", message);
        }
    }

    /**
     * Mapea un DTO de lectura (TutorCursoDTO) a un DTO de actualización (TutorCursoUpdateDTO).
     */
    private TutorCursoUpdateDTO mapToUpdateDTO(TutorCursoDTO tutorDTO) {
        TutorCursoUpdateDTO updateDTO = new TutorCursoUpdateDTO();
        updateDTO.setId(tutorDTO.getId());
        updateDTO.setNombre(tutorDTO.getNombre());
        updateDTO.setApellidos(tutorDTO.getApellidos());
        updateDTO.setDni(tutorDTO.getDni()); 
        updateDTO.setEmail(tutorDTO.getEmail());
        updateDTO.setTelefono(tutorDTO.getTelefono());
        updateDTO.setEspecialidad(tutorDTO.getEspecialidad());
        updateDTO.setActivo(tutorDTO.getActivo());

        return updateDTO;
    }
}