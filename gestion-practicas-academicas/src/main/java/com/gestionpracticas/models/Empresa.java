package com.gestionpracticas.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "empresa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El CIF es obligatorio")
    @Pattern(regexp = "^[A-Z][0-9]{7}[A-Z0-9]$", message = "El CIF debe tener un formato válido")
    @Column(nullable = false, unique = true, length = 12)
    private String cif;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(length = 200)
    private String direccion;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    @Column(length = 15)
    private String telefono;

    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(length = 100)
    private String email;

    @Size(max = 100, message = "La persona de contacto no puede exceder 100 caracteres")
    @Column(name = "persona_contacto", length = 100)
    private String personaContacto;

    @Size(max = 50, message = "El sector no puede exceder 50 caracteres")
    @Column(length = 50)
    private String sector;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Relaciones
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Alumno> alumnos;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<TutorPracticas> tutoresPracticas;
}