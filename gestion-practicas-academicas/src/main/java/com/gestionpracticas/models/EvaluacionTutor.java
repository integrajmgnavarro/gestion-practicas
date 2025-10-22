package com.gestionpracticas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "evaluacion_tutor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionTutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_practicas_id", nullable = false)
    @NotNull(message = "El tutor de prácticas es obligatorio")
    private TutorPracticas tutorPracticas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_curso_id", nullable = false)
    @NotNull(message = "El tutor de curso es obligatorio")
    private TutorCurso tutorCurso;

    @DecimalMin(value = "0.00", message = "La puntuación no puede ser negativa")
    @DecimalMax(value = "10.00", message = "La puntuación no puede exceder 10")
    @Column(precision = 4, scale = 2)
    private BigDecimal puntuacion;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Lob
    @Column(name = "aspectos_positivos", columnDefinition = "TEXT")
    private String aspectosPositivos;

    @Lob
    @Column(name = "aspectos_mejorar", columnDefinition = "TEXT")
    private String aspectosMejorar;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;
}