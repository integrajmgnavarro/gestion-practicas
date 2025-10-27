package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String cif;
    private String direccion;
    private String telefono;
    private String email;
    private String personaContacto;
    private String sector;
    private Boolean activo;
    
    // Metadata
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}