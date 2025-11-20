package com.gestionpracticas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa el Reporte Narrativo o Feedback detallado
 * generado por el Tutor del Curso y dirigido al Alumno, separándolo
 * de las evaluaciones basadas en capacidades (Evaluacion) y las
 * evaluaciones a otros tutores (EvaluacionTutor).
 */
@Entity
@Table(name = "reporte_alumno")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAlumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relaciones Mandatorias ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    @NotNull(message = "El alumno receptor del reporte es obligatorio")
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_curso_id", nullable = false)
    @NotNull(message = "El tutor de curso que emite el reporte es obligatorio")
    private TutorCurso tutorCurso;

    // --- Contenido del Reporte ---
    @Column(length = 255)
    @NotNull(message = "El título del reporte es obligatorio")
    private String titulo;

    @Lob
    @Column(name = "resumen_ejecutivo", columnDefinition = "TEXT")
    private String resumenEjecutivo; // Un párrafo de resumen del desempeño

    @Lob
    @Column(name = "aspectos_destacados", columnDefinition = "TEXT")
    private String aspectosDestacados; // Funciona como Aspectos Positivos

    @Lob
    @Column(name = "areas_mejora", columnDefinition = "TEXT")
    private String areasMejora; // Funciona como Aspectos a Mejorar/Recomendaciones

    // --- Metadatos ---
    @NotNull(message = "La fecha de emisión es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaEmision;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
