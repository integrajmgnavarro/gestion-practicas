package com.gestionpracticas.dto;

import lombok.Data;

@Data
public class EmpresaSearchDTO {
    private String nombre;
    private String cif;
    private String sector;
    private Boolean activo;
}