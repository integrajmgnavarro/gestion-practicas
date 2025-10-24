package com.gestionpracticas.dto;

import java.math.BigDecimal;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriterioEvaluacionUpdateDTO {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Lob
    private String descripcion;

    @DecimalMin(value = "0.00", message = "El peso no puede ser negativo")
    @DecimalMax(value = "100.00", message = "El peso no puede exceder 100")
    private BigDecimal peso; // porcentaje sobre la nota final
    
    //Metadata
    private Boolean activo;
}
