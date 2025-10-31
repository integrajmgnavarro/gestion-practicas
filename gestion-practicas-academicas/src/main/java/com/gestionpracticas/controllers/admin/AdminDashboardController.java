package com.gestionpracticas.controllers.admin;

import com.gestionpracticas.dto.EstadisticasGeneralesDTO;
import com.gestionpracticas.dto.EstadisticasEmpleabilidadDTO;
import com.gestionpracticas.dto.ReporteEjecutivoDTO;
import com.gestionpracticas.services.EstadisticasService;
import com.gestionpracticas.services.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {
    // Solo se mantienen los servicios necesarios para Dashboard, Estadísticas y Reportes
    private final EstadisticasService estadisticasService;
    private final ReportsService reportsService;

    // ========================= DASHBOARD ========================= //

    /**
     * GET /admin/ o /admin/dashboard
     * Dashboard principal del administrador
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        EstadisticasGeneralesDTO estadisticas = estadisticasService.getEstadisticasGenerales();
        ReporteEjecutivoDTO reporteEjecutivo = reportsService.getReporteEjecutivo();

        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("reporteEjecutivo", reporteEjecutivo);

        return "admin/dashboard";
    }

    // ========================= ESTADÍSTICAS Y REPORTES ========================= //

    @GetMapping("/estadisticas")
    public String estadisticas(Model model) {
        EstadisticasGeneralesDTO estadisticas = estadisticasService.getEstadisticasGenerales();
        EstadisticasEmpleabilidadDTO empleabilidad = estadisticasService.getEstadisticasEmpleabilidad();

        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("empleabilidad", empleabilidad);

        return "admin/estadisticas";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        return "admin/reportes";
    }

    @GetMapping("/reportes/ejecutivo")
    public String reporteEjecutivo(Model model) {
        ReporteEjecutivoDTO reporte = reportsService.getReporteEjecutivo();
        model.addAttribute("reporte", reporte);
        return "admin/reporte-ejecutivo";
    }

    // ========================= API REST (Estadísticas y Reportes) ========================= //
    
    @GetMapping("/estadisticas/api")
    @ResponseBody
    public ResponseEntity<EstadisticasGeneralesDTO> getEstadisticasApi() {
        EstadisticasGeneralesDTO estadisticas = estadisticasService.getEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }
    
    @GetMapping("/reportes/ejecutivo/api")
    @ResponseBody
    public ResponseEntity<ReporteEjecutivoDTO> getReporteEjecutivoApi() {
        ReporteEjecutivoDTO reporte = reportsService.getReporteEjecutivo();
        return ResponseEntity.ok(reporte);
    }
}