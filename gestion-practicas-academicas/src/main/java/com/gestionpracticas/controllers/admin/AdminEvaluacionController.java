package com.gestionpracticas.controllers.admin;

import com.gestionpracticas.dto.CapacidadEvaluacionDTO;
import com.gestionpracticas.dto.CriterioEvaluacionCreateDTO;
import com.gestionpracticas.dto.CriterioEvaluacionDTO;
import com.gestionpracticas.services.EvaluacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/criterios-evaluacion")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminEvaluacionController {

    private final EvaluacionService evaluacionService;

    @GetMapping
    public String criteriosEvaluacion(Model model) {
        List<CriterioEvaluacionDTO> criterios = evaluacionService.getAllCriteriosEvaluacion();
        model.addAttribute("criterios", criterios);
        return "admin/criterios-evaluacion";
    }

    @GetMapping("/nuevo")
    public String nuevoCriterio(Model model) {
        model.addAttribute("criterioCreateDTO", new CriterioEvaluacionCreateDTO());
        return "admin/criterio-form";
    }

    @PostMapping
    public String createCriterio(@Valid @ModelAttribute CriterioEvaluacionCreateDTO createDTO,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            return "admin/criterio-form";
        }

        evaluacionService.createCriterioEvaluacion(createDTO);
        return "redirect:/admin/criterios-evaluacion?success=true";
    }

    // Nota: Aunque la ruta es diferente, se mantiene en este controlador por ser lógica de evaluación.
    @GetMapping("/capacidades-evaluacion") 
    public String capacidadesEvaluacion(Model model) {
        List<CapacidadEvaluacionDTO> capacidades = evaluacionService.getAllCapacidadesEvaluacion();
        model.addAttribute("capacidades", capacidades);
        return "admin/capacidades-evaluacion";
    }
    // TODO: Faltan métodos de edición y eliminación (PUT/DELETE) para Criterios y el CRUD completo para Capacidades
}