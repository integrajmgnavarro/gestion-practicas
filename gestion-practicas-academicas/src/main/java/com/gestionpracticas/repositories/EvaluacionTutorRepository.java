package com.gestionpracticas.repositories;

import com.gestionpracticas.models.EvaluacionTutor;
import com.gestionpracticas.models.TutorCurso;
import com.gestionpracticas.models.TutorPracticas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluacionTutorRepository extends JpaRepository<EvaluacionTutor, Long> {

    // =============================
    // 🔹 BÚSQUEDAS BÁSICAS
    // =============================

    List<EvaluacionTutor> findByTutorPracticas(TutorPracticas tutorPracticas);

    List<EvaluacionTutor> findByTutorCurso(TutorCurso tutorCurso);

    List<EvaluacionTutor> findByFecha(LocalDate fecha);

    List<EvaluacionTutor> findByFechaBetween(LocalDate inicio, LocalDate fin);

    // =============================
    // 🔹 RELACIONES / FETCH
    // =============================

    /**
     * Recupera una evaluación de tutor junto con sus relaciones (para evitar N+1).
     */
    @Query("SELECT et FROM EvaluacionTutor et " +
           "LEFT JOIN FETCH et.tutorPracticas " +
           "LEFT JOIN FETCH et.tutorCurso " +
           "WHERE et.id = :id")
    Optional<EvaluacionTutor> findByIdWithRelations(@Param("id") Long id);

    /**
     * Obtiene todas las evaluaciones de un tutor de prácticas, con los tutores de curso asociados.
     */
    @Query("SELECT et FROM EvaluacionTutor et " +
           "LEFT JOIN FETCH et.tutorCurso " +
           "WHERE et.tutorPracticas.id = :tutorPracticasId")
    List<EvaluacionTutor> findByTutorPracticasIdWithRelations(@Param("tutorPracticasId") Long tutorPracticasId);

    // =============================
    // 🔹 AGREGADOS / ESTADÍSTICAS
    // =============================

    @Query("SELECT COUNT(et) FROM EvaluacionTutor et")
    Long countTotal();

    @Query("SELECT COUNT(et) FROM EvaluacionTutor et WHERE et.tutorPracticas.id = :tutorPracticasId")
    Long countByTutorPracticas(@Param("tutorPracticasId") Long tutorPracticasId);

    @Query("SELECT COUNT(et) FROM EvaluacionTutor et WHERE et.tutorCurso.id = :tutorCursoId")
    Long countByTutorCurso(@Param("tutorCursoId") Long tutorCursoId);

    @Query("SELECT AVG(et.puntuacion) FROM EvaluacionTutor et WHERE et.tutorPracticas.id = :tutorPracticasId")
    BigDecimal averageByTutorPracticas(@Param("tutorPracticasId") Long tutorPracticasId);

    @Query("SELECT AVG(et.puntuacion) FROM EvaluacionTutor et WHERE et.tutorCurso.id = :tutorCursoId")
    BigDecimal averageByTutorCurso(@Param("tutorCursoId") Long tutorCursoId);

    // =============================
    // 🔹 BÚSQUEDA MULTICRITERIO
    // =============================
    /**
     * Búsqueda flexible por tutor de prácticas, tutor de curso, rango de fechas y rango de puntuación.
     */
    @Query("SELECT et FROM EvaluacionTutor et WHERE " +
           "(:tutorPracticasId IS NULL OR et.tutorPracticas.id = :tutorPracticasId) AND " +
           "(:tutorCursoId IS NULL OR et.tutorCurso.id = :tutorCursoId) AND " +
           "(:fechaInicio IS NULL OR et.fecha >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR et.fecha <= :fechaFin) AND " +
           "(:puntuacionMin IS NULL OR et.puntuacion >= :puntuacionMin) AND " +
           "(:puntuacionMax IS NULL OR et.puntuacion <= :puntuacionMax)")
    List<EvaluacionTutor> findByMultipleCriteria(
            @Param("tutorPracticasId") Long tutorPracticasId,
            @Param("tutorCursoId") Long tutorCursoId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("puntuacionMin") BigDecimal puntuacionMin,
            @Param("puntuacionMax") BigDecimal puntuacionMax
    );
}
