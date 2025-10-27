package com.gestionpracticas.repositories;

import com.gestionpracticas.models.CapacidadEvaluacion;
import com.gestionpracticas.models.CriterioEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapacidadEvaluacionRepository extends JpaRepository<CapacidadEvaluacion, Long> {

    // =============================
    // 🔹 BÚSQUEDAS BÁSICAS
    // =============================

    Optional<CapacidadEvaluacion> findByNombre(String nombre);

    List<CapacidadEvaluacion> findByNombreContainingIgnoreCase(String nombre);    

    // =============================
    // 🔹 RELACIONES
    // =============================

    /**
     * Obtiene todas las capacidades pertenecientes a un criterio específico.
     */
    List<CapacidadEvaluacion> findByCriterio(CriterioEvaluacion criterio);
    List<CapacidadEvaluacion> findByCriterio_Id(Long criterioId);

    /**
     * Busca todas las capacidades activas pertenecientes a un criterio.
     */
    List<CapacidadEvaluacion> findByCriterio_IdAndActivoTrue(Long criterioId);

    /**
     * Recupera una capacidad junto a su criterio asociado (fetch join para evitar N+1).
     */
    @Query("SELECT c FROM CapacidadEvaluacion c " +
           "LEFT JOIN FETCH c.criterio " +
           "WHERE c.id = :id")
    Optional<CapacidadEvaluacion> findByIdWithCriterio(@Param("id") Long id);

    // =============================
    // 🔹 FILTROS DE ESTADO
    // =============================

    List<CapacidadEvaluacion> findByActivo(Boolean activo);

    List<CapacidadEvaluacion> findByActivoTrue();

    // =============================
    // 🔹 AGREGADOS / CONTADORES
    // =============================

    @Query("SELECT COUNT(c) FROM CapacidadEvaluacion c WHERE c.activo = true")
    Long countCapacidadesActivas();

    @Query("SELECT COUNT(c) FROM CapacidadEvaluacion c WHERE c.criterio.id = :criterioId AND c.activo = true")
    Long countCapacidadesActivasByCriterio(@Param("criterioId") Long criterioId);

    @Query("SELECT COUNT(c) FROM CapacidadEvaluacion c WHERE LOWER(c.nombre) = LOWER(:nombre) AND c.criterio.id = :criterioId")
    Long countByNombreAndCriterio(@Param("nombre") String nombre, @Param("criterioId") Long criterioId);

    // =============================
    // 🔹 BÚSQUEDA MULTICRITERIO
    // =============================
    /**
     * Búsqueda flexible por nombre, criterio, puntuación máxima y estado activo.
     * Si un parámetro es NULL, no se aplica ese filtro.
     */
    @Query("SELECT ce FROM CapacidadEvaluacion ce WHERE " +
           "(:nombre IS NULL OR LOWER(ce.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:criterioId IS NULL OR ce.criterio.id = :criterioId) AND " +
           "(:puntuacionMaxima IS NULL OR ce.puntuacionMaxima = :puntuacionMaxima) AND " +
           "(:activo IS NULL OR ce.activo = :activo)")
    List<CapacidadEvaluacion> findByMultipleCriteria(
            @Param("nombre") String nombre,
            @Param("criterioId") Long criterioId,
            @Param("puntuacionMaxima") Integer puntuacionMaxima,
            @Param("activo") Boolean activo
    );
}
