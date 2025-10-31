package com.gestionpracticas.controllers.admin;

import com.gestionpracticas.dto.EmpresaCreateDTO;
import com.gestionpracticas.dto.EmpresaDTO;
import com.gestionpracticas.dto.EmpresaUpdateDTO;
import com.gestionpracticas.exception.BusinessException;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.services.EmpresaService;
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
@RequestMapping("/admin/empresas")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminEmpresaController {

    private final EmpresaService empresaService;

    /**
     * Muestra la lista de todas las empresas.
     * GET /admin/empresas
     */
    @GetMapping
    public String listarEmpresas(Model model) {
        try {
            List<EmpresaDTO> empresas = empresaService.getAllEmpresas();
            model.addAttribute("empresas", empresas);
            return "admin/empresas"; // Asume la vista: admin/empresas.html
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la lista de empresas.");
            return "admin/empresas";
        }
    }

    /**
     * Muestra el formulario para crear una nueva empresa.
     * GET /admin/empresas/nuevo
     */
    @GetMapping("/nuevo")
    public String nuevaEmpresaForm(Model model) {
        model.addAttribute("empresaCreateDTO", new EmpresaCreateDTO());
        return "admin/empresa-form"; // Asume la vista: admin/empresa-form.html
    }

    /**
     * Procesa la creación de una nueva empresa.
     * POST /admin/empresas
     */
    @PostMapping
    public String createEmpresa(@Valid @ModelAttribute("empresaCreateDTO") EmpresaCreateDTO createDTO,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/empresa-form";
        }

        try {
            empresaService.createEmpresa(createDTO);
            redirectAttributes.addFlashAttribute("message", "Empresa '" + createDTO.getNombre() + "' creada exitosamente.");
            return "redirect:/admin/empresas";
        } catch (DuplicateResourceException e) {
            result.rejectValue("cif", "duplicate", e.getMessage());
            return "admin/empresa-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al crear la empresa.");
            return "redirect:/admin/empresas";
        }
    }

    /**
     * Muestra el formulario para editar una empresa existente.
     * GET /admin/empresas/{id}/editar
     */
    @GetMapping("/{id}/editar")
    public String editarEmpresaForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            EmpresaDTO empresaDTO = empresaService.getEmpresaById(id);
            
            // Mapeamos EmpresaDTO a EmpresaUpdateDTO
            EmpresaUpdateDTO updateDTO = new EmpresaUpdateDTO();
            updateDTO.setId(empresaDTO.getId());
            updateDTO.setNombre(empresaDTO.getNombre());
            updateDTO.setCif(empresaDTO.getCif());
            updateDTO.setDireccion(empresaDTO.getDireccion());
            updateDTO.setTelefono(empresaDTO.getTelefono());
            updateDTO.setEmail(empresaDTO.getEmail());
            updateDTO.setPersonaContacto(empresaDTO.getPersonaContacto());
            updateDTO.setSector(empresaDTO.getSector());
            updateDTO.setActivo(empresaDTO.getActivo());

            model.addAttribute("empresaUpdateDTO", updateDTO);
            return "admin/empresa-edit-form"; // Asume la vista: admin/empresa-edit-form.html
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/empresas";
        }
    }

    /**
     * Procesa la actualización de una empresa existente.
     * POST /admin/empresas/{id}/editar
     */
    @PostMapping("/{id}/editar")
    public String updateEmpresa(@PathVariable Long id,
                                @Valid @ModelAttribute("empresaUpdateDTO") EmpresaUpdateDTO updateDTO,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        updateDTO.setId(id); // Aseguramos que el ID de la URL sea el que se use

        if (result.hasErrors()) {
            return "admin/empresa-edit-form";
        }

        try {
            empresaService.updateEmpresa(id, updateDTO);
            redirectAttributes.addFlashAttribute("message", "Empresa '" + updateDTO.getNombre() + "' actualizada exitosamente.");
            return "redirect:/admin/empresas";
        } catch (DuplicateResourceException e) {
            result.rejectValue("cif", "duplicate", e.getMessage());
            return "admin/empresa-edit-form";
        } catch (ResourceNotFoundException e) {
             model.addAttribute("error", e.getMessage());
             return "admin/empresa-edit-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al actualizar la empresa.");
            return "redirect:/admin/empresas";
        }
    }

    /**
     * Elimina una empresa.
     * POST /admin/empresas/{id}/eliminar
     */
    @PostMapping("/{id}/eliminar")
    public String deleteEmpresa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            empresaService.deleteEmpresa(id);
            redirectAttributes.addFlashAttribute("message", "Empresa eliminada exitosamente.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (BusinessException e) {
            // Captura el error de integridad (alumnos o tutores asociados)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al eliminar la empresa.");
        }
        return "redirect:/admin/empresas";
    }
}
