package com.gestionpracticas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "capacidad_evaluacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacidadEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criterio_id", nullable = false)
    @NotNull(message = "El criterio es obligatorio")
    private CriterioEvaluacion criterio;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Min(value = 1, message = "La puntuación máxima debe ser al menos 1")
    @Column(name = "puntuacion_maxima")
    private Integer puntuacionMaxima = 10;

    @Column(nullable = false)
    private Boolean activo = true;

    // Relaciones
    @OneToMany(mappedBy = "capacidad", cascade = CascadeType.ALL)
    private List<Evaluacion> evaluaciones;
}