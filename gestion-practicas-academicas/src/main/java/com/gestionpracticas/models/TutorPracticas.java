package com.gestionpracticas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tutor_practicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorPracticas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellidos;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI debe tener 8 números seguidos de una letra mayúscula")
    @Column(nullable = false, unique = true, length = 9)
    private String dni;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    @Column(length = 15)
    private String telefono;

    @Size(max = 100, message = "El cargo no puede exceder 100 caracteres")
    @Column(length = 100)
    private String cargo;

    //Datos practicas
    @Size(max = 200, message = "El horario no puede exceder 200 caracteres")
    @Column(length = 200)
    private String horario;

    //Metadatos
    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    @OneToMany(mappedBy = "tutorPracticas", cascade = CascadeType.ALL)
    private List<Alumno> alumnos;

    @OneToMany(mappedBy = "tutorPracticas", cascade = CascadeType.ALL)
    private List<Incidencia> incidencias;

    @OneToMany(mappedBy = "tutorPracticas", cascade = CascadeType.ALL)
    private List<Evaluacion> evaluaciones;

    @OneToMany(mappedBy = "tutorPracticas", cascade = CascadeType.ALL)
    private List<EvaluacionTutor> evaluacionesTutor;

    @OneToOne(mappedBy = "tutorPracticas")
    private Usuario usuario;

    // Método de utilidad para obtener el nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }
}