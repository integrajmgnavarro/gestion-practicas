package com.gestionpracticas.controllers;

import com.gestionpracticas.dto.ObservacionDiariaCreateDTO;
import com.gestionpracticas.dto.ObservacionDiariaDTO;
import com.gestionpracticas.dto.ObservacionDiariaUpdateDTO;
import com.gestionpracticas.services.ObservacionDiariaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para manejar las Observaciones Diarias de las prácticas.
 */
@RestController
@RequestMapping("/api/observaciones")
public class ObservacionDiariaController {

    @Autowired
    private ObservacionDiariaService observacionDiariaService;

    // =============================================
    // 🔹 GET: Obtener y Buscar
    // =============================================

    /**
     * Obtiene todas las observaciones diarias.
     */
    @GetMapping
    public ResponseEntity<List<ObservacionDiariaDTO>> getAllObservaciones() {
        List<ObservacionDiariaDTO> observaciones = observacionDiariaService.findAll();
        return ResponseEntity.ok(observaciones);
    }

    /**
     * Obtiene una observación diaria por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ObservacionDiariaDTO> getObservacionById(@PathVariable Long id) {
        return observacionDiariaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =============================================
    // 🔹 POST: Crear
    // =============================================

    /**
     * Crea una nueva Observación Diaria.
     */
    @PostMapping
    public ResponseEntity<ObservacionDiariaDTO> createObservacion(@Valid @RequestBody ObservacionDiariaCreateDTO dto) {
        try {
            ObservacionDiariaDTO newObservacion = observacionDiariaService.createObservacion(dto);
            return new ResponseEntity<>(newObservacion, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            // Error 404 si el alumno referenciado no existe
            return ResponseEntity.notFound().build();
        }
    }

    // =============================================
    // 🔹 PUT/PATCH: Actualizar
    // =============================================

    /**
     * Actualiza completamente o parcialmente una Observación Diaria existente.
     * Utilizamos el DTO de actualización que permite campos opcionales.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ObservacionDiariaDTO> updateObservacion(@PathVariable Long id, 
                                                                  @Valid @RequestBody ObservacionDiariaUpdateDTO dto) {
        // Aseguramos que el ID del path coincida con el ID del DTO (si se proporciona)
        dto.setId(id);
        try {
            ObservacionDiariaDTO updatedObservacion = observacionDiariaService.updateObservacion(id, dto);
            return ResponseEntity.ok(updatedObservacion);
        } catch (EntityNotFoundException e) {
            // Error 404 si la observación no existe
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Error 400 Bad Request por otras validaciones (ej. horas fuera de rango)
            return ResponseEntity.badRequest().build();
        }
    }


    // =============================================
    // 🔹 DELETE: Eliminar
    // =============================================

    /**
     * Elimina una Observación Diaria por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObservacion(@PathVariable Long id) {
        try {
            observacionDiariaService.deleteObservacion(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
