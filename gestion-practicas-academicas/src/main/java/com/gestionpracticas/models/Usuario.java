package com.gestionpracticas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Usuario {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
    
    @Column(name = "referenceId")
    private Long referenceId;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "ultimoAcceso")
    private LocalDateTime ultimoAcceso;
    
    @Column(name = "fechaCreacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fechaActualizacion")
    private LocalDateTime fechaActualizacion;

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
    
    //Relaciones
    @OneToOne
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;
    
    @OneToOne
    @JoinColumn(name = "tutorCurso_id")
    private TutorCurso tutorCurso;
    
    @OneToOne
    @JoinColumn(name = "tutorPracticas_id")
    private TutorPracticas tutorPracticas;
    
    @OneToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
    
 // Enum interno para los roles
    public enum Rol {
        ADMIN,
        TUTOR_CURSO,
        TUTOR_PRACTICAS,
        ALUMNO
    }
}
