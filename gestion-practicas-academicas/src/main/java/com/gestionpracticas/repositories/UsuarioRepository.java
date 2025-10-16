package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Usuario;
import com.gestionpracticas.models.Usuario.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar usuario por email
    Optional<Usuario> findByEmail(String email);
    
    // Buscar usuarios por rol
    List<Usuario> findByRol(Rol rol);
    
    // Buscar usuarios activos
    List<Usuario> findByActivo(Boolean activo);
    
    // Buscar por rol y activo
    List<Usuario> findByRolAndActivo(Rol rol, Boolean activo);
    
    // Verificar si existe un email
    boolean existsByEmail(String email);
    
    // Buscar por referenceId y rol
    Optional<Usuario> findByReferenceIdAndRol(Long referenceId, Rol rol);
}
