package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Empresa;
import com.gestionpracticas.models.TutorPracticas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorPracticasRepository extends JpaRepository<TutorPracticas, Long> {

    // Búsquedas básicas
    Optional<TutorPracticas> findByDni(String dni);
    Optional<TutorPracticas> findByEmail(String email);
    Optional<TutorPracticas> findByUsuarioId(Long usuarioId);

    // Verificaciones de existencia
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);

    // Búsquedas por relaciones
    List<TutorPracticas> findByEmpresa(Empresa empresa);
    List<TutorPracticas> findByEmpresaId(Long empresaId);

    List<TutorPracticas> findByCargo(String cargo);
    List<TutorPracticas> findByCargoContainingIgnoreCase(String cargo);

    // Búsquedas por estado
    List<TutorPracticas> findByActivo(Boolean activo);

    // Búsquedas combinadas
    List<TutorPracticas> findByEmpresaIdAndActivo(Long empresaId, Boolean activo);

    // Consultas personalizadas con JPQL
    @Query("SELECT COUNT(a) FROM TutorPracticas a WHERE a.empresa.id = :empresaId AND a.activo = true")
    Long countTutorPracticasActivosByEmpresa(@Param("empresaId") Long empresaId);

    // Búsqueda con múltiples criterios
    
    @Query("SELECT tp FROM TutorPracticas tp WHERE " +
           "(:nombre IS NULL OR LOWER(tp.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:apellidos IS NULL OR LOWER(tp.apellidos) LIKE LOWER(CONCAT('%', :apellidos, '%'))) AND " +
           "(:dni IS NULL OR tp.dni = :dni) AND " +
           "(:empresaId IS NULL OR tp.empresa.id = :empresaId) AND " +
           "(:activo IS NULL OR tp.activo = :activo)")
    List<TutorPracticas> findByMultipleCriteria(
            @Param("nombre") String nombre,
            @Param("apellidos") String apellidos,
            @Param("dni") String dni,
            @Param("empresaId") Long empresaId,
            @Param("activo") Boolean activo
    );
}