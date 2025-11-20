package com.gestionpracticas.controllers.admin;

import com.gestionpracticas.dto.EmpresaDTO;
import com.gestionpracticas.dto.TutorPracticasCreateDTO;
import com.gestionpracticas.dto.TutorPracticasDTO;
import com.gestionpracticas.dto.TutorPracticasUpdateDTO;
import com.gestionpracticas.exception.BusinessException;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.services.EmpresaService;
import com.gestionpracticas.services.TutorPracticasService;
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

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de tutores de prácticas en la sección de administración.
 * Expone la ruta base /admin/tutores-practicas.
 */
@Controller
// RUTA CONSOLIDADA: Usamos la ruta PLURAL como base estándar para la gestión de la colección.
@RequestMapping("/admin/tutores-practicas")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminTutorPracticasController {

    private final TutorPracticasService tutorPracticasService;
    private final EmpresaService empresaService;

    // ========================= MÉTODOS PRIVADOS DE UTILIDAD ========================= //

    /**
     * Carga la lista de todas las empresas para el selector de los formularios.
     * @param model El modelo de la vista.
     */
    private void cargarEmpresas(Model model) {
        try {
            // Asumiendo que el servicio tiene un método para obtener todas las empresas.
            List<EmpresaDTO> empresas = empresaService.getAllEmpresas();
            model.addAttribute("empresas", empresas);
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo cargar la lista de empresas.");
        }
    }

    /**
     * Maneja errores de DNI/Email duplicado para mostrarlos en el campo correcto
     * del formulario mediante el BindingResult.
     * @param e La excepción de recurso duplicado.
     * @param result El resultado del binding.
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

    /**
     * Mapea un DTO de lectura (TutorPracticasDTO) a un DTO de actualización (TutorPracticasUpdateDTO).
     * @param tutorDTO El DTO de lectura con los datos actuales.
     * @return El DTO de actualización listo para ser usado en el formulario.
     */
    private TutorPracticasUpdateDTO mapToUpdateDTO(TutorPracticasDTO tutorDTO) {
        TutorPracticasUpdateDTO updateDTO = new TutorPracticasUpdateDTO();
        updateDTO.setId(tutorDTO.getId());
        updateDTO.setNombre(tutorDTO.getNombre());
        updateDTO.setApellidos(tutorDTO.getApellidos());
        updateDTO.setDni(tutorDTO.getDni());
        updateDTO.setEmail(tutorDTO.getEmail());
        updateDTO.setTelefono(tutorDTO.getTelefono());
        updateDTO.setCargo(tutorDTO.getCargo());
        updateDTO.setHorario(tutorDTO.getHorario());
        updateDTO.setActivo(tutorDTO.getActivo());
        // Se asume que getEmpresaId() es accesible en TutorPracticasDTO
        updateDTO.setEmpresaId(tutorDTO.getEmpresaId()); 
        return updateDTO;
    }

    // --- LISTADO (Ruta: /admin/tutores-practicas) ---

    /**
     * Muestra la lista de tutores de prácticas con paginación y filtros.
     * GET /admin/tutores-practicas
     */
    @GetMapping
    public String listarTutores(
            Model model,
            // Parámetros de paginación
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // Parámetros de filtro
            @RequestParam Optional<String> nombre,
            @RequestParam Optional<String> apellidos,
            @RequestParam Optional<String> dni,
            @RequestParam Optional<Boolean> activo,
            @RequestParam Optional<String> sortField,
            @RequestParam Optional<String> sortDir
    ) {
        try {
            // 1. Configuración de la paginación y ordenación
            String field = sortField.orElse("apellidos");
            String direction = sortDir.orElse("asc");
            Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(field).ascending() : Sort.by(field).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            // 2. Llamada al servicio con filtros y paginación
            // Se utiliza findTutoresByFilters del servicio para aplicar los criterios.
            Page<TutorPracticasDTO> tutoresPage = tutorPracticasService.findTutoresByFilters(
                    nombre.orElse(""),
                    apellidos.orElse(""),
                    dni.orElse(""),
                    activo.orElse(null),
                    pageable
            );

            // 3. Añadir al modelo los resultados y metadatos de paginación
            model.addAttribute("tutoresPage", tutoresPage);
            model.addAttribute("currentPage", tutoresPage.getNumber());
            model.addAttribute("totalPaginas", tutoresPage.getTotalPages());
            model.addAttribute("totalElements", tutoresPage.getTotalElements());
            model.addAttribute("pageSize", size);

            // Re-inyectar filtros y ordenación en el modelo para mantener el estado
            model.addAttribute("nombre", nombre.orElse(""));
            model.addAttribute("apellidos", apellidos.orElse(""));
            model.addAttribute("dni", dni.orElse(""));
            model.addAttribute("activo", activo.orElse(null));
            model.addAttribute("sortField", field);
            model.addAttribute("sortDir", direction);


            // VISTA: Devuelve el nombre de la plantilla del listado
            return "admin/tutor-practicas";

        } catch (Exception e) {
            System.err.println("Error al cargar la lista de tutores de prácticas: " + e.getMessage());
            model.addAttribute("error", "Error al cargar la lista de tutores de prácticas.");
            model.addAttribute("tutoresPage", Page.empty());

            // Añadir atributos de paginación mínimos para evitar errores en la vista
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPaginas", 0);
            model.addAttribute("totalElements", 0L);
            model.addAttribute("pageSize", size);

            return "admin/tutor-practicas";
        }
    }

    // --- CREACIÓN (Ruta: /admin/tutores-practicas/nuevo) ---

    /**
     * Muestra el formulario para crear un nuevo tutor de prácticas.
     * GET /admin/tutores-practicas/nuevo
     */
    @GetMapping("/nuevo")
    public String nuevoTutorForm(Model model) {
        // IMPORTANTE: Mantenemos "tutorCreateDTO" para compatibilidad con la plantilla
        model.addAttribute("tutorCreateDTO", new TutorPracticasCreateDTO());
        cargarEmpresas(model);
        // VISTA: Devuelve la plantilla de formulario de creación
        return "admin/tutor-practicas-form";
    }

    /**
     * Procesa la creación de un nuevo tutor de prácticas.
     * POST /admin/tutores-practicas
     */
    @PostMapping
    public String createTutor(@Valid @ModelAttribute("tutorCreateDTO") TutorPracticasCreateDTO createDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarEmpresas(model);
            // VISTA: Si hay errores, vuelve al formulario
            return "admin/tutor-practicas-form";
        }

        try {
            tutorPracticasService.createTutorPracticas(createDTO);
            redirectAttributes.addFlashAttribute("message", "Tutor '" + createDTO.getNombre() + " " + createDTO.getApellidos() + "' creado exitosamente.");
            // REDIRECCIÓN: Redirige a la URL PLURAL
            return "redirect:/admin/tutores-practicas";
        } catch (DuplicateResourceException e) {
            manejarErroresUnicidad(e, result);
            cargarEmpresas(model);
            // VISTA: Si hay errores, vuelve al formulario
            return "admin/tutor-practicas-form";
        } catch (ResourceNotFoundException e) {
            // El error es del campo empresaId si la empresa no existe
            result.rejectValue("empresaId", "notfound", "La empresa seleccionada no existe.");
            cargarEmpresas(model);
            // VISTA: Si hay errores, vuelve al formulario
            return "admin/tutor-practicas-form";
        } catch (Exception e) {
            System.err.println("Error al crear el tutor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error inesperado al crear el tutor.");
            // REDIRECCIÓN: Redirige a la URL PLURAL
            return "redirect:/admin/tutores-practicas";
        }
    }

    // --- EDICIÓN (Ruta: /admin/tutores-practicas/{id}/editar) ---

    /**
     * Muestra el formulario para editar un tutor de prácticas.
     * GET /admin/tutores-practicas/{id}/editar
     */
    @GetMapping("/{id}/editar")
    public String editarTutorForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TutorPracticasDTO tutorDTO = tutorPracticasService.getTutorPracticasById(id);
            TutorPracticasUpdateDTO updateDTO = mapToUpdateDTO(tutorDTO);
            // IMPORTANTE: Mantenemos "tutorPracticasUpdateDTO" para compatibilidad con la plantilla
            model.addAttribute("tutorPracticasUpdateDTO", updateDTO);
            cargarEmpresas(model);

            // VISTA: Devuelve la plantilla de formulario de edición
            return "admin/tutor-practicas-edit-form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // REDIRECCIÓN: Redirige a la URL PLURAL
            return "redirect:/admin/tutores-practicas";
        }
    }

    /**
     * Procesa la actualización de un tutor existente.
     * POST /admin/tutores-practicas/{id}/editar
     */
    @PostMapping("/{id}/editar")
    public String updateTutor(@PathVariable Long id,
                              @Valid @ModelAttribute("tutorPracticasUpdateDTO") TutorPracticasUpdateDTO updateDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        updateDTO.setId(id);

        if (result.hasErrors()) {
            cargarEmpresas(model);
            // VISTA: Si hay errores, vuelve al formulario de edición
            return "admin/tutor-practicas-edit-form";
        }

        try {
            tutorPracticasService.updateTutorPracticas(id, updateDTO);
            redirectAttributes.addFlashAttribute("message", "Tutor actualizado exitosamente.");
            // REDIRECCIÓN: Redirige a la URL PLURAL
            return "redirect:/admin/tutores-practicas";
        } catch (DuplicateResourceException e) {
            manejarErroresUnicidad(e, result);
            cargarEmpresas(model);
            // VISTA: Si hay errores de unicidad, vuelve al formulario de edición
            return "admin/tutor-practicas-edit-form";
        } catch (ResourceNotFoundException e) {
            // Puede ser el tutor o la empresa
            model.addAttribute("error", e.getMessage());
            cargarEmpresas(model);
            // VISTA: Si hay errores, vuelve al formulario de edición
            return "admin/tutor-practicas-edit-form";
        } catch (Exception e) {
            System.err.println("Error al actualizar el tutor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error inesperado al actualizar el tutor: " + e.getMessage());
            // REDIRECCIÓN: Redirige a la URL PLURAL
            return "redirect:/admin/tutores-practicas";
        }
    }

    // --- ELIMINACIÓN (Ruta: /admin/tutores-practicas/{id}/eliminar) ---

    /**
     * Elimina un tutor.
     * POST /admin/tutores-practicas/{id}/eliminar
     */
    @PostMapping("/{id}/eliminar")
    public String deleteTutor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tutorPracticasService.deleteTutorPracticas(id);
            redirectAttributes.addFlashAttribute("message", "Tutor de Prácticas eliminado exitosamente.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (BusinessException e) {
            // Atrapa errores de negocio específicos (ej. si el tutor tiene prácticas asignadas)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al eliminar el tutor: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error inesperado al eliminar el tutor.");
        }
        // REDIRECCIÓN: Redirige a la URL PLURAL
        return "redirect:/admin/tutores-practicas";
    }
}