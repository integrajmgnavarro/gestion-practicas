package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Evaluacion;
import com.gestionpracticas.models.Alumno;
import com.gestionpracticas.models.TutorPracticas;
import com.gestionpracticas.models.CapacidadEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    // =============================
    // 🔹 BÚSQUEDAS BÁSICAS
    // =============================

    List<Evaluacion> findByAlumno(Alumno alumno);
    List<Evaluacion> findByAlumnoId(Long alumnoId);
    
    List<Evaluacion> findByCapacidad(CapacidadEvaluacion capacidad);

    List<Evaluacion> findByTutorPracticas(TutorPracticas tutorPracticas);
    List<Evaluacion> findByTutorPracticasId(Long tutorPracticasId);

    List<Evaluacion> findByFecha(LocalDate fecha);

    List<Evaluacion> findByFechaBetween(LocalDate inicio, LocalDate fin);

    // =============================
    // 🔹 RELACIONES / FETCH
    // =============================

    /**
     * Recupera una evaluación junto con su alumno, tutor y capacidad (para evitar N+1).
     */
    @Query("SELECT e FROM Evaluacion e " +
           "LEFT JOIN FETCH e.alumno " +
           "LEFT JOIN FETCH e.tutorPracticas " +
           "LEFT JOIN FETCH e.capacidad " +
           "WHERE e.id = :id")
    Optional<Evaluacion> findByIdWithRelations(@Param("id") Long id);

    /**
     * Obtiene todas las evaluaciones de un alumno con sus relaciones cargadas.
     */
    @Query("SELECT e FROM Evaluacion e " +
           "LEFT JOIN FETCH e.capacidad " +
           "LEFT JOIN FETCH e.tutorPracticas " +
           "WHERE e.alumno.id = :alumnoId")
    List<Evaluacion> findByAlumnoIdWithRelations(@Param("alumnoId") Long alumnoId);

    // =============================
    // 🔹 AGREGADOS / ESTADÍSTICAS
    // =============================

    @Query("SELECT COUNT(e) FROM Evaluacion e WHERE e.alumno.id = :alumnoId")
    Long countByAlumno(@Param("alumnoId") Long alumnoId);

    @Query("SELECT AVG(e.puntuacion) FROM Evaluacion e WHERE e.alumno.id = :alumnoId")
    BigDecimal averagePuntuacionByAlumno(@Param("alumnoId") Long alumnoId);

    @Query("SELECT AVG(e.puntuacion) FROM Evaluacion e WHERE e.capacidad.id = :capacidadId")
    BigDecimal averagePuntuacionByCapacidad(@Param("capacidadId") Long capacidadId);

    // =============================
    // 🔹 CONSULTAS DE ESTADO
    // =============================

    /**
     * Busca todas las evaluaciones con puntuación nula (no calificadas aún).
     */
    @Query("SELECT e FROM Evaluacion e WHERE e.puntuacion IS NULL")
    List<Evaluacion> findPendientesDeEvaluar();

    /**
     * Busca evaluaciones ya calificadas (puntuación no nula).
     */
    @Query("SELECT e FROM Evaluacion e WHERE e.puntuacion IS NOT NULL")
    List<Evaluacion> findEvaluadas();

    // =============================
    // 🔹 BÚSQUEDA MULTICRITERIO
    // =============================
    /**
     * Búsqueda flexible por alumno, tutor, capacidad, rango de fechas y estado de puntuación.
     */
    @Query("SELECT e FROM Evaluacion e WHERE " +
           "(:alumnoId IS NULL OR e.alumno.id = :alumnoId) AND " +
           "(:tutorPracticasId IS NULL OR e.tutorPracticas.id = :tutorPracticasId) AND " +
           "(:capacidadId IS NULL OR e.capacidad.id = :capacidadId) AND " +
           "(:fechaInicio IS NULL OR e.fecha >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR e.fecha <= :fechaFin) AND " +
           "(:evaluadas IS NULL OR (:evaluadas = TRUE AND e.puntuacion IS NOT NULL)" + 
           " OR (:evaluadas = FALSE AND e.puntuacion IS NULL))")
    List<Evaluacion> findByMultipleCriteria(
            @Param("alumnoId") Long alumnoId,
            @Param("tutorPracticasId") Long tutorPracticasId,
            @Param("capacidadId") Long capacidadId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("evaluadas") Boolean evaluadas
    );
}
