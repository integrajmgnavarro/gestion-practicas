package com.gestionpracticas.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioDTO {
    private Long id;
    private String email;
    private String rol;
    private Long referenceId;
    private Boolean activo;
    private LocalDateTime ultimoAcceso;
    private LocalDateTime fechaCreacion;
}