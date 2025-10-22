package com.gestionpracticas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    @NotNull(message = "El alumno es obligatorio")
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_practicas_id")
    private TutorPracticas tutorPracticas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capacidad_id", nullable = false)
    @NotNull(message = "La capacidad es obligatoria")
    private CapacidadEvaluacion capacidad;

    @DecimalMin(value = "0.00", message = "La puntuación no puede ser negativa")
    @DecimalMax(value = "10.00", message = "La puntuación no puede exceder 10")
    @Column(precision = 4, scale = 2)
    private BigDecimal puntuacion;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}