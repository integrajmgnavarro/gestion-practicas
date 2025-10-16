package com.gestionpracticas.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity //esto crea una Entidad JPA en la database
@Table(name = "test_conexion")

public class TestConnection {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String mensaje;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    public TestConnection() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() 
    	{ return id; }
    public void setId(Long id) 
    	{ this.id = id; }
    public String getMensaje() 
    	{ return mensaje; }
    public void setMensaje(String mensaje) 
    	{ this.mensaje = mensaje; }
    public LocalDateTime getFechaCreacion() 
    	{ return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) 
    	{ this.fechaCreacion = fechaCreacion; }
}
