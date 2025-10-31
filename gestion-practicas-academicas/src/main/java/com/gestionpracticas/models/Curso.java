package com.gestionpracticas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "curso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    // Campo añadido: Código del curso (generalmente único)
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    @Column(nullable = false, length = 20, unique = true)
    private String codigo;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @Min(value = 1, message = "La duración debe ser al menos 1 hora")
    @Column(name = "duracion")
    private Integer duracion; // en horas

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_curso_id")
    private TutorCurso tutorCurso;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Relaciones - CAMBIO CRÍTICO: Eliminamos CascadeType.ALL.
    // Para eliminar un curso, la lógica de negocio debe forzar a que
    // la lista de alumnos esté vacía primero, para evitar eliminación masiva.
    @OneToMany(mappedBy = "curso", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Alumno> alumnos;
}
