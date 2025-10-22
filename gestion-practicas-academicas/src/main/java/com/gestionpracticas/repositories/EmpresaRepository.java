package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    // =============================
    // 🔹 BÚSQUEDAS BÁSICAS
    // =============================

    Optional<Empresa> findByCif(String cif);
    List<Empresa> findByNombreContainingIgnoreCase(String nombre);
    Optional<Empresa> findByEmail(String email);

    // =============================
    // 🔹 VERIFICACIONES
    // =============================

    boolean existsByCif(String cif);
    boolean existsByEmail(String email);

    // =============================
    // 🔹 FILTROS DE ESTADO
    // =============================

    List<Empresa> findByActivo(Boolean activo);

    // =============================
    // 🔹 CONSULTAS PERSONALIZADAS
    // =============================

    @Query("SELECT COUNT(e) FROM Empresa e WHERE e.activo = true")
    Long countEmpresasActivas();

    @Query("SELECT e FROM Empresa e WHERE e.activo = true AND LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Empresa> searchEmpresasActivasByNombre(@Param("nombre") String nombre);

    // =============================
    // 🔹 BÚSQUEDA MULTICRITERIO
    // =============================

    @Query("SELECT e FROM Empresa e WHERE " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:cif IS NULL OR e.cif = :cif) AND " +
           "(:email IS NULL OR e.email = :email) AND " +
           "(:activo IS NULL OR e.activo = :activo)")
    List<Empresa> findByMultipleCriteria(
            @Param("nombre") String nombre,
            @Param("cif") String cif,
            @Param("email") String email,
            @Param("activo") Boolean activo
    );
}
