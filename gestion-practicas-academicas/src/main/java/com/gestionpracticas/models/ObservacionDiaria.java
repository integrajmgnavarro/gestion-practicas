package com.gestionpracticas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "observacion_diaria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObservacionDiaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    @NotNull(message = "El alumno es obligatorio")
    private Alumno alumno;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String actividades;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String explicaciones;

    @Lob
    @Column(name = "observaciones_alumno", columnDefinition = "TEXT")
    private String observacionesAlumno;

    @Lob
    @Column(name = "observaciones_tutor", columnDefinition = "TEXT")
    private String observacionesTutor;

    @Min(value = 0, message = "Las horas realizadas no pueden ser negativas")
    @Max(value = 24, message = "Las horas realizadas no pueden exceder 24")
    @Column(name = "horas_realizadas")
    private Integer horasRealizadas;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}