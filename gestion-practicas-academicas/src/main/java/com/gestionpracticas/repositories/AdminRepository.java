package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Búsquedas básicas
    Optional<Admin> findByDni(String dni);
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByUsuario_Id(Long usuarioId);

    // Verificaciones de existencia
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);  
    // Buscar usuario activos
    List<Admin> findByActivo(boolean activo);


}

