package com.gestionpracticas.controllers.admin;

import com.gestionpracticas.dto.AlumnoCreateDTO;
import com.gestionpracticas.dto.AlumnoDTO;
import com.gestionpracticas.dto.AlumnoUpdateDTO;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.services.AlumnoService;
import com.gestionpracticas.services.CursoService;
import com.gestionpracticas.services.EmpresaService;
import com.gestionpracticas.services.TutorCursoService;
import com.gestionpracticas.services.TutorPracticasService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la gestión de alumnos en el panel de administración.
 */
@Controller
@RequestMapping("/admin/alumnos")
@RequiredArgsConstructor
@Slf4j
public class AdminAlumnoController {

    private final AlumnoService alumnoService;
    private final CursoService cursoService;
    private final EmpresaService empresaService;
    private final TutorPracticasService tutorPracticasService;
    private final TutorCursoService tutorCursoService;

    /**
     * Carga las listas de entidades (cursos, empresas, tutores) necesarias 
     * para los dropdowns de los formularios de creación y edición.
     * @param model El modelo de Spring.
     */
    private void loadRelatedEntities(Model model) {
        model.addAttribute("cursos", cursoService.getAllCursos());
        model.addAttribute("empresas", empresaService.getAllEmpresas());
        model.addAttribute("tutoresPracticas", tutorPracticasService.getAllTutoresPracticas());
        model.addAttribute("tutoresCurso", tutorCursoService.findAllList());
    }

    // ==========================================================
    // 1. LISTADO, BÚSQUEDA Y PAGINACIÓN (GET /admin/alumnos)
    // ==========================================================
    @GetMapping
    public String listAlumnos(
            // Parámetros de búsqueda generales
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String dni,
            
            // NUEVOS PARÁMETROS DE FILTRO POR RELACIÓN
            @RequestParam(required = false) Long cursoId,
            @RequestParam(required = false) Long empresaId,
            
            @RequestParam(required = false, name = "activo") Boolean activo,
            // Parámetros de paginación
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Ordenar por Apellidos y Nombre por defecto.
        Pageable pageable = PageRequest.of(page, size, Sort.by("apellidos").ascending().and(Sort.by("nombre").ascending()));

        // Llamar al método findAlumnosByCriteria del servicio
        Page<AlumnoDTO> alumnosPage = alumnoService.findAlumnosByCriteria(
                nombre,
                apellidos,
                dni,
                cursoId, // Filtro de Curso
                empresaId, // Filtro de Empresa
                activo,
                pageable
        );

        model.addAttribute("alumnosPage", alumnosPage);
        
        // Carga la lista completa para el bloque de estadísticas.
        model.addAttribute("alumnos", alumnoService.getAllAlumnos()); 

        return "admin/alumnos";
    }

    // ==========================================================
    // 2. FORMULARIO DE CREACIÓN (GET /admin/alumnos/nuevo)
    // ==========================================================
    @GetMapping("/nuevo")
    public String showNewAlumnoForm(Model model) {
        // Para creación, usamos AlumnoCreateDTO
        model.addAttribute("alumno", new AlumnoCreateDTO());
        model.addAttribute("isEdit", false); // <-- NUEVO: Modo Creación
        loadRelatedEntities(model); // Cargar listas para dropdowns
        return "admin/alumno-form";
    }

    // ==========================================================
    // 3. CREACIÓN (POST /admin/alumnos/nuevo)
    // ==========================================================
    @PostMapping("/nuevo")
    public String createAlumno(
            @Valid @ModelAttribute("alumno") AlumnoCreateDTO createDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            loadRelatedEntities(model); // Recargar datos relacionados en caso de error
            model.addAttribute("isEdit", false); // Mantener el modo Creación en caso de error de validación
            return "admin/alumno-form";
        }

        try {
            alumnoService.createAlumno(createDTO);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Alumno '" + createDTO.getNombre() + " " + createDTO.getApellidos() + "' creado exitosamente.");
            return "redirect:/admin/alumnos";

        } catch (DuplicateResourceException e) {
            log.warn("Error de duplicidad al crear alumno: {}", e.getMessage());
            if (e.getMessage().contains("DNI")) {
                 result.rejectValue("dni", "error.dni.duplicado", e.getMessage());
            } else if (e.getMessage().contains("email")) {
                 result.rejectValue("email", "error.email.duplicado", e.getMessage());
            } else {
                 model.addAttribute("errorMessage", e.getMessage());
            }
            
            loadRelatedEntities(model);
            model.addAttribute("isEdit", false); // Mantener el modo Creación en caso de error
            return "admin/alumno-form";
        } catch (Exception e) {
            log.error("Error al crear alumno", e);
            model.addAttribute("errorMessage", "Ocurrió un error: " + e.getMessage());
            loadRelatedEntities(model);
            model.addAttribute("isEdit", false); // Mantener el modo Creación en caso de error
            return "admin/alumno-form";
        }
    }

    // ==========================================================
    // 4. FORMULARIO DE EDICIÓN (GET /admin/alumnos/{id}/editar)
    // ==========================================================
    @GetMapping("/{id}/editar") 
    public String showEditAlumnoForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Para edición, usamos AlumnoUpdateDTO
            AlumnoUpdateDTO updateDTO = alumnoService.findAlumnoUpdateDTOById(id);
            model.addAttribute("alumno", updateDTO);
            
            model.addAttribute("isEdit", true); // <-- NUEVO: Modo Edición
            loadRelatedEntities(model); // Cargar listas para dropdowns
            
            return "admin/alumno-form";

        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/admin/alumnos";
        }
    }

    // ==========================================================
    // 5. EDICIÓN (POST /admin/alumnos/{id}/editar)
    // ==========================================================
    @PostMapping("/{id}/editar") 
    public String updateAlumno(
            @PathVariable Long id,
            @Valid @ModelAttribute("alumno") AlumnoUpdateDTO updateDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Asegurar que el ID del path y el DTO coincidan
        updateDTO.setId(id);

        if (result.hasErrors()) {
            loadRelatedEntities(model);
            model.addAttribute("isEdit", true); // Mantener el modo Edición en caso de error de validación
            return "admin/alumno-form";
        }

        try {
            alumnoService.updateAlumno(updateDTO);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Alumno con ID " + id + " actualizado exitosamente.");
            return "redirect:/admin/alumnos";

        } catch (ResourceNotFoundException e) {
            log.error("Error al actualizar alumno: Recurso no encontrado o ID no existe", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error de recurso: " + e.getMessage());
            return "redirect:/admin/alumnos";

        } catch (DuplicateResourceException e) {
            log.warn("Error de duplicidad al actualizar alumno: {}", e.getMessage());
            if (e.getMessage().contains("DNI")) {
                 result.rejectValue("dni", "error.dni.duplicado", e.getMessage());
            } else if (e.getMessage().contains("email")) {
                 result.rejectValue("email", "error.email.duplicado", e.getMessage());
            }
            loadRelatedEntities(model);
            model.addAttribute("isEdit", true); // Mantener el modo Edición en caso de error
            return "admin/alumno-form";
        }
    }
    
    // ==========================================================
    // 6. ELIMINACIÓN (POST /admin/alumnos/eliminar/{id})
    // ==========================================================
    @PostMapping("/eliminar/{id}")
    public String deleteAlumno(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            alumnoService.deleteAlumno(id);
            redirectAttributes.addFlashAttribute("successMessage", "Alumno con ID " + id + " eliminado correctamente.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error al eliminar alumno con ID {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar el alumno con ID " + id + " debido a un error.");
        }
        return "redirect:/admin/alumnos";
    }
}