package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por email (para login, validaciones, etc.)
    Optional<Usuario> findByEmail(String email);

    // Verificar si ya existe un email registrado
    boolean existsByEmail(String email);

    // Buscar usuarios por rol
    List<Usuario> findByRol(Usuario.Rol rol);

    // Buscar usuario asociado a una entidad (Alumno, Tutor, etc.)
    Optional<Usuario> findByReferenceId(Long referenceId);
    
    // Buscar usuario activos
    List<Usuario> findByActivo(boolean activo);

}

