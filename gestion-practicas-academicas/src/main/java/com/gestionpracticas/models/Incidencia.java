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
@Table(name = "incidencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incidencia {

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

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipo de incidencia es obligatorio")
    @Column(nullable = false, length = 30)
    private TipoIncidencia tipo;

    @NotBlank(message = "La descripción es obligatoria")
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String resolucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoIncidencia estado = EstadoIncidencia.ABIERTA;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    // Enums
    public enum TipoIncidencia {
        FALTA,
        RETRASO,
        PROBLEMA_ACTITUD,
        OTROS
    }

    public enum EstadoIncidencia {
        ABIERTA,
        EN_PROCESO,
        RESUELTA
    }
}