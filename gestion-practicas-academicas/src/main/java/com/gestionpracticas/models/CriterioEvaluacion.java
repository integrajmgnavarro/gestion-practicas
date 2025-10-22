package com.gestionpracticas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "criterio_evaluacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriterioEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.00", message = "El peso no puede ser negativo")
    @DecimalMax(value = "100.00", message = "El peso no puede exceder 100")
    @Column(precision = 5, scale = 2)
    private BigDecimal peso; // porcentaje sobre la nota final

    @Column(nullable = false)
    private Boolean activo = true;

    // Relaciones
    @OneToMany(mappedBy = "criterio", cascade = CascadeType.ALL)
    private List<CapacidadEvaluacion> capacidades;
}