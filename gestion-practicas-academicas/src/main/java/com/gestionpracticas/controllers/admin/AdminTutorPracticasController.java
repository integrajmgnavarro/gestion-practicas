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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/tutores-practicas")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminTutorPracticasController {

    private final TutorPracticasService tutorPracticasService;
    private final EmpresaService empresaService; // Necesario para cargar el selector de Empresa

    /**
     * Muestra la lista de todos los tutores de prácticas.
     * GET /admin/tutores-practicas
     */
    @GetMapping
    public String listarTutores(Model model) {
        try {
            List<TutorPracticasDTO> tutores = tutorPracticasService.getAllTutoresPracticas();
            model.addAttribute("tutores", tutores);
            return "admin/tutores-practicas-list"; // Vista: admin/tutores-practicas-list.html
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la lista de tutores de prácticas.");
            return "admin/tutores-practicas-list";
        }
    }

    /**
     * Muestra el formulario para crear un nuevo tutor de prácticas.
     * GET /admin/tutores-practicas/nuevo
     */
    @GetMapping("/nuevo")
    public String nuevoTutorForm(Model model) {
        model.addAttribute("tutorCreateDTO", new TutorPracticasCreateDTO());
        cargarEmpresas(model);
        return "admin/tutor-practicas-form"; // Vista: admin/tutor-practicas-form.html
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
            return "admin/tutor-practicas-form";
        }

        try {
            tutorPracticasService.createTutorPracticas(createDTO);
            redirectAttributes.addFlashAttribute("message", "Tutor '" + createDTO.getNombre() + " " + createDTO.getApellidos() + "' creado exitosamente.");
            return "redirect:/admin/tutores-practicas";
        } catch (DuplicateResourceException e) {
            // Error de DNI o Email duplicado
            manejarErroresUnicidad(e, result);
            cargarEmpresas(model);
            return "admin/tutor-practicas-form";
        } catch (ResourceNotFoundException e) {
            // Error si la empresa no existe
            result.rejectValue("empresaId", "notfound", "La empresa seleccionada no existe.");
            cargarEmpresas(model);
            return "admin/tutor-practicas-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al crear el tutor.");
            return "redirect:/admin/tutores-practicas";
        }
    }

    /**
     * Muestra el formulario para editar un tutor de prácticas.
     * GET /admin/tutores-practicas/{id}/editar
     */
    @GetMapping("/{id}/editar")
    public String editarTutorForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TutorPracticasDTO tutorDTO = tutorPracticasService.getTutorPracticasById(id);

            // Mapeamos TutorPracticasDTO a TutorPracticasUpdateDTO
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
            updateDTO.setEmpresaId(tutorDTO.getEmpresaId());

            model.addAttribute("tutorUpdateDTO", updateDTO);
            cargarEmpresas(model);
            return "admin/tutor-practicas-edit-form"; // Vista: admin/tutor-practicas-edit-form.html
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/tutores-practicas";
        }
    }

    /**
     * Procesa la actualización de un tutor existente.
     * POST /admin/tutores-practicas/{id}/editar
     */
    @PostMapping("/{id}/editar")
    public String updateTutor(@PathVariable Long id,
                              @Valid @ModelAttribute("tutorUpdateDTO") TutorPracticasUpdateDTO updateDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        updateDTO.setId(id); // Aseguramos que el ID de la URL sea el que se use

        if (result.hasErrors()) {
            cargarEmpresas(model);
            return "admin/tutor-practicas-edit-form";
        }

        try {
            // Adaptamos la llamada al servicio para usar solo el DTO, ya que updateDTO contiene el ID.
            tutorPracticasService.updateTutorPracticas(updateDTO); 
            redirectAttributes.addFlashAttribute("message", "Tutor actualizado exitosamente.");
            return "redirect:/admin/tutores-practicas";
        } catch (DuplicateResourceException e) {
            manejarErroresUnicidad(e, result);
            cargarEmpresas(model);
            return "admin/tutor-practicas-edit-form";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            cargarEmpresas(model);
            return "admin/tutor-practicas-edit-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al actualizar el tutor.");
            return "redirect:/admin/tutores-practicas";
        }
    }

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
            // Captura el error de integridad (alumnos, incidencias, etc., asociados)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al eliminar el tutor.");
        }
        return "redirect:/admin/tutores-practicas";
    }
    
    // ========================= MÉTODOS PRIVADOS DE UTILIDAD ========================= //
    
    /**
     * Carga la lista de empresas activas para el selector del formulario.
     */
    private void cargarEmpresas(Model model) {
        try {
            List<EmpresaDTO> empresas = empresaService.getAllEmpresas();
            model.addAttribute("empresas", empresas);
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo cargar la lista de empresas.");
        }
    }

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
