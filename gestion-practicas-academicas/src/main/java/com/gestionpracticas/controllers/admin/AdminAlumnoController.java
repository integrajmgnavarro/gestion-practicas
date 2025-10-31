package com.gestionpracticas.controllers.admin;

import com.gestionpracticas.dto.AlumnoUpdateDTO;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.services.AlumnoService;
import com.gestionpracticas.services.CursoService;
import com.gestionpracticas.services.EmpresaService;
import com.gestionpracticas.services.TutorCursoService;
import com.gestionpracticas.services.TutorPracticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAlumnoController {

    private final AlumnoService alumnoService;
    private final CursoService cursoService;
    private final EmpresaService empresaService;
    private final TutorPracticasService tutorPracticasService;
    private final TutorCursoService tutorCursoService;

    // ... (otros métodos como listarAlumnos, guardarAlumno, etc.)

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        try {
            // 1. Obtener la ENTIDAD Alumno usando getAlumnoEntityById() 
            // y luego mapearla al DTO de actualización. (CORRECCIÓN CLAVE)
            AlumnoUpdateDTO alumnoUpdateDTO = AlumnoService.toUpdateDTO(alumnoService.getAlumnoEntityById(id));
            
            // 2. Cargar listas de selectores para los desplegables del formulario
            // Nota: Se asume que estos métodos getAll...() devuelven una lista de DTOs o entidades simples con ID y Nombre.
            model.addAttribute("cursos", cursoService.getAllCursos());
            model.addAttribute("empresas", empresaService.getAllEmpresas());
            model.addAttribute("tutoresPracticas", tutorPracticasService.getAllTutoresPracticas()); 
            model.addAttribute("tutoresCurso", tutorCursoService.getAllTutoresCurso());
            
            // 3. Añadir el objeto 'alumno' (DTO) al Model
            model.addAttribute("alumno", alumnoUpdateDTO);
            
            return "admin/alumno-edit";
            
        } catch (ResourceNotFoundException e) {
            // Manejar la excepción si el recurso no se encuentra
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/alumnos"; 
        }
    }
    
    // ... (otros métodos como manejar la edición POST, etc.)
}
