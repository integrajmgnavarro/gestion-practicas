package com.gestionpracticas.controllers;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.services.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reportes")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportsService reportsService;

    // ========================= VISTAS ========================= //

    /**
     * GET /reportes
     * Página principal de reportes
     */
    @GetMapping
    public String reportesPage(Model model) {
        return "reportes/index";
    }

    // ========================= REPORTES POR CURSO ========================= //

    /**
     * GET /reportes/curso/{id}
     * Vista del reporte de un curso
     */
    @GetMapping("/curso/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public String reporteCurso(@PathVariable Long id, Model model) {
        ReporteCursoDTO reporte = reportsService.getReporteCurso(id);
        model.addAttribute("reporte", reporte);
        return "reportes/curso";
    }

    /**
     * GET /reportes/curso/{id}/api
     * Reporte de curso en JSON
     */
    @GetMapping("/curso/{id}/api")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    @ResponseBody
    public ResponseEntity<ReporteCursoDTO> reporteCursoApi(@PathVariable Long id) {
        ReporteCursoDTO reporte = reportsService.getReporteCurso(id);
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /reportes/curso/{id}/pdf
     * Descarga reporte de curso en PDF
     */
    @GetMapping("/curso/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<byte[]> reporteCursoPdf(@PathVariable Long id) {
        // TODO: Implementar generación de PDF
        // byte[] pdfBytes = pdfService.generarReporteCurso(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-curso-" + id + ".pdf");
        
        // return ResponseEntity.ok().headers(headers).body(pdfBytes);
        return ResponseEntity.status(501).body(null); // Not Implemented
    }

    // ========================= REPORTES POR EMPRESA ========================= //

    /**
     * GET /reportes/empresa/{id}
     * Vista del reporte de una empresa
     */
    @GetMapping("/empresa/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public String reporteEmpresa(@PathVariable Long id, Model model) {
        ReporteEmpresaDTO reporte = reportsService.getReporteEmpresa(id);
        model.addAttribute("reporte", reporte);
        return "reportes/empresa";
    }

    /**
     * GET /reportes/empresa/{id}/api
     * Reporte de empresa en JSON
     */
    @GetMapping("/empresa/{id}/api")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    @ResponseBody
    public ResponseEntity<ReporteEmpresaDTO> reporteEmpresaApi(@PathVariable Long id) {
        ReporteEmpresaDTO reporte = reportsService.getReporteEmpresa(id);
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /reportes/empresa/{id}/excel
     * Descarga reporte de empresa en Excel
     */
    @GetMapping("/empresa/{id}/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public ResponseEntity<byte[]> reporteEmpresaExcel(@PathVariable Long id) {
        // TODO: Implementar generación de Excel
        // byte[] excelBytes = excelService.generarReporteEmpresa(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte-empresa-" + id + ".xlsx");
        
        // return ResponseEntity.ok().headers(headers).body(excelBytes);
        return ResponseEntity.status(501).body(null); // Not Implemented
    }

    // ========================= REPORTES POR TUTOR DE PRÁCTICAS ========================= //

    /**
     * GET /reportes/tutor-practicas/{id}
     * Vista del reporte de un tutor de prácticas
     */
    @GetMapping("/tutor-practicas/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public String reporteTutorPracticas(@PathVariable Long id, Model model) {
        ReporteTutorPracticasDTO reporte = reportsService.getReporteTutorPracticas(id);
        model.addAttribute("reporte", reporte);
        return "reportes/tutor-practicas";
    }

    /**
     * GET /reportes/tutor-practicas/{id}/api
     * Reporte de tutor de prácticas en JSON
     */
    @GetMapping("/tutor-practicas/{id}/api")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    @ResponseBody
    public ResponseEntity<ReporteTutorPracticasDTO> reporteTutorPracticasApi(@PathVariable Long id) {
        ReporteTutorPracticasDTO reporte = reportsService.getReporteTutorPracticas(id);
        return ResponseEntity.ok(reporte);
    }

    // ========================= REPORTES POR ALUMNO ========================= //

    /**
     * GET /reportes/alumno/{id}
     * Vista del reporte de un alumno
     */
    @GetMapping("/alumno/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS', 'ALUMNO')")
    public String reporteAlumno(@PathVariable Long id, Model model) {
        // TODO: Verificar que el usuario tiene permiso para ver este alumno
        ReporteAlumnoDTO reporte = reportsService.getReporteAlumno(id);
        model.addAttribute("reporte", reporte);
        return "reportes/alumno";
    }

    /**
     * GET /reportes/alumno/{id}/api
     * Reporte de alumno en JSON
     */
    @GetMapping("/alumno/{id}/api")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS', 'ALUMNO')")
    @ResponseBody
    public ResponseEntity<ReporteAlumnoDTO> reporteAlumnoApi(@PathVariable Long id) {
        ReporteAlumnoDTO reporte = reportsService.getReporteAlumno(id);
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /reportes/alumno/{id}/pdf
     * Descarga ficha completa del alumno en PDF
     */
    @GetMapping("/alumno/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO', 'TUTOR_PRACTICAS')")
    public ResponseEntity<byte[]> reporteAlumnoPdf(@PathVariable Long id) {
        // TODO: Implementar generación de PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ficha-alumno-" + id + ".pdf");
        
        return ResponseEntity.status(501).body(null); // Not Implemented
    }

    // ========================= REPORTE EJECUTIVO ========================= //

    /**
     * GET /reportes/ejecutivo
     * Vista del reporte ejecutivo
     */
    @GetMapping("/ejecutivo")
    @PreAuthorize("hasRole('ADMIN')")
    public String reporteEjecutivo(Model model) {
        ReporteEjecutivoDTO reporte = reportsService.getReporteEjecutivo();
        model.addAttribute("reporte", reporte);
        return "reportes/ejecutivo";
    }

    /**
     * GET /reportes/ejecutivo/api
     * Reporte ejecutivo en JSON
     */
    @GetMapping("/ejecutivo/api")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<ReporteEjecutivoDTO> reporteEjecutivoApi() {
        ReporteEjecutivoDTO reporte = reportsService.getReporteEjecutivo();
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /reportes/ejecutivo/pdf
     * Descarga reporte ejecutivo en PDF
     */
    @GetMapping("/ejecutivo/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> reporteEjecutivoPdf() {
        // TODO: Implementar generación de PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-ejecutivo.pdf");
        
        return ResponseEntity.status(501).body(null); // Not Implemented
    }

    // ========================= REPORTES COMPARATIVOS ========================= //

    /**
     * GET /reportes/comparativo/cursos
     * Vista de comparación entre cursos
     */
    @GetMapping("/comparativo/cursos")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public String comparativoCursos(@RequestParam(required = false) Long cursoId1,
                                    @RequestParam(required = false) Long cursoId2,
                                    Model model) {
        if (cursoId1 != null && cursoId2 != null) {
            ReporteCursoDTO reporte1 = reportsService.getReporteCurso(cursoId1);
            ReporteCursoDTO reporte2 = reportsService.getReporteCurso(cursoId2);
            model.addAttribute("reporte1", reporte1);
            model.addAttribute("reporte2", reporte2);
        }
        
        return "reportes/comparativo-cursos";
    }

    /**
     * GET /reportes/comparativo/empresas
     * Vista de comparación entre empresas
     */
    @GetMapping("/comparativo/empresas")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR_CURSO')")
    public String comparativoEmpresas(@RequestParam(required = false) Long empresaId1,
                                      @RequestParam(required = false) Long empresaId2,
                                      Model model) {
        if (empresaId1 != null && empresaId2 != null) {
            ReporteEmpresaDTO reporte1 = reportsService.getReporteEmpresa(empresaId1);
            ReporteEmpresaDTO reporte2 = reportsService.getReporteEmpresa(empresaId2);
            model.addAttribute("reporte1", reporte1);
            model.addAttribute("reporte2", reporte2);
        }
        
        return "reportes/comparativo-empresas";
    }

    // ========================= EXPORTACIONES MASIVAS ========================= //

    /**
     * GET /reportes/exportar/todos-alumnos
     * Exporta listado completo de alumnos
     */
    @GetMapping("/exportar/todos-alumnos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportarTodosAlumnos(@RequestParam(defaultValue = "excel") String formato) {
        // TODO: Implementar exportación masiva
        HttpHeaders headers = new HttpHeaders();
        
        if ("excel".equals(formato)) {
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "listado-alumnos.xlsx");
        } else if ("pdf".equals(formato)) {
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "listado-alumnos.pdf");
        }
        
        return ResponseEntity.status(501).body(null); // Not Implemented
    }
}