package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Incidencia;
import com.gestionpracticas.models.Alumno;
import com.gestionpracticas.models.TutorPracticas;
import com.gestionpracticas.models.Incidencia.EstadoIncidencia;
import com.gestionpracticas.models.Incidencia.TipoIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    // =============================
    // 🔹 BÚSQUEDAS BÁSICAS
    // =============================

    List<Incidencia> findByAlumno(Alumno alumno);
    List<Incidencia> findByAlumno_Id(Long alumnoId);
    long countByAlumnoId(Long alumnoId);

    // NUEVO: Para buscar incidencias de una lista de alumnos (necesario en el servicio)
    List<Incidencia> findByAlumno_IdIn(List<Long> alumnoIds);

    List<Incidencia> findByTutorPracticas(TutorPracticas tutorPracticas);

    List<Incidencia> findByFecha(LocalDate fecha);

    List<Incidencia> findByFechaBetween(LocalDate inicio, LocalDate fin);

    List<Incidencia> findByEstado(EstadoIncidencia estado);

    List<Incidencia> findByTipo(TipoIncidencia tipo);

    // =============================
    // 🔹 RELACIONES / FETCH
    // =============================

    /**
     * Recupera una incidencia junto con su alumno y tutor (para evitar N+1).
     */
    @Query("SELECT i FROM Incidencia i " +
           "LEFT JOIN FETCH i.alumno " +
           "LEFT JOIN FETCH i.tutorPracticas " +
           "WHERE i.id = :id")
    Optional<Incidencia> findByIdWithRelations(@Param("id") Long id);

    /**
     * Recupera todas las incidencias de un alumno con tutor cargado.
     */
    @Query("SELECT i FROM Incidencia i " +
           "LEFT JOIN FETCH i.tutorPracticas " +
           "WHERE i.alumno.id = :alumnoId " +
           "ORDER BY i.fecha DESC")
    List<Incidencia> findByAlumno_IdWithRelations(@Param("alumnoId") Long alumnoId);

    // =============================
    // 🔹 AGREGADOS / ESTADÍSTICAS
    // =============================

    @Query("SELECT COUNT(i) FROM Incidencia i WHERE i.alumno.id = :alumnoId")
    Long countByAlumno(@Param("alumnoId") Long alumnoId);

    @Query("SELECT COUNT(i) FROM Incidencia i WHERE i.tutorPracticas.id = :tutorPracticasId")
    Long countByTutorPracticas(@Param("tutorPracticasId") Long tutorPracticasId);

    @Query("SELECT COUNT(i) FROM Incidencia i WHERE i.estado = :estado")
    Long countByEstado(@Param("estado") EstadoIncidencia estado);

    @Query("SELECT COUNT(i) FROM Incidencia i WHERE i.tipo = :tipo")
    Long countByTipo(@Param("tipo") TipoIncidencia tipo);

    @Query("SELECT COUNT(i) FROM Incidencia i WHERE i.estado = 'RESUELTA'")
    Long countIncidenciasResueltas();

    @Query("SELECT COUNT(i) FROM Incidencia i WHERE i.estado != 'RESUELTA'")
    Long countIncidenciasPendientes();

    // =============================
    // 🔹 BÚSQUEDA MULTICRITERIO
    // =============================
    /**
     * Búsqueda flexible por alumno, tutor, tipo, estado y rango de fechas.
     * Si un parámetro es NULL, no se aplica ese filtro.
     */
    @Query("SELECT i FROM Incidencia i WHERE " +
           "(:alumnoId IS NULL OR i.alumno.id = :alumnoId) AND " +
           "(:tutorPracticasId IS NULL OR i.tutorPracticas.id = :tutorPracticasId) AND " +
           "(:tipo IS NULL OR i.tipo = :tipo) AND " +
           "(:estado IS NULL OR i.estado = :estado) AND " +
           "(:fechaInicio IS NULL OR i.fecha >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR i.fecha <= :fechaFin)")
    List<Incidencia> findByMultipleCriteria(
             @Param("alumnoId") Long alumnoId,
             @Param("tutorPracticasId") Long tutorPracticasId,
             @Param("tipo") TipoIncidencia tipo,
             @Param("estado") EstadoIncidencia estado,
             @Param("fechaInicio") LocalDate fechaInicio,
             @Param("fechaFin") LocalDate fechaFin
    );
}
