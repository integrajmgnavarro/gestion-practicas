package com.gestionpracticas.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Asegura que solo los ADMIN puedan acceder
@RequiredArgsConstructor
public class AdminController {

    /**
     * Maneja la ruta base del administrador: GET /admin
     * Devuelve la vista principal (dashboard).
     */
    @GetMapping
    public String adminDashboard() {
        // Asumiendo que tu plantilla se llama dashboard.html dentro de la carpeta admin/
        return "admin/dashboard"; 
    }
}