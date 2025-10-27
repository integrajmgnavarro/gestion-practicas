package com.gestionpracticas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Mapea la ruta raíz (GET /). Devuelve la plantilla login.html
    @GetMapping("/")
    public String index() {
        return "login"; // Carga login.html
    }
    
    // Eliminamos el método duplicado @GetMapping("/login") ya que la SecurityConfig
    // ahora lo manejará directamente cuando no se necesiten parámetros extra.
}
