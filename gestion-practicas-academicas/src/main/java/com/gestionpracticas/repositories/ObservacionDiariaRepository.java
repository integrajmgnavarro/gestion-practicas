package com.gestionpracticas.repositories;

import com.gestionpracticas.models.ObservacionDiaria;
import com.gestionpracticas.models.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ObservacionDiariaRepository extends JpaRepository<ObservacionDiaria, Long> {

    // =============================
    // 🔹 BÚSQUEDAS BÁSICAS
    // =============================

    List<ObservacionDiaria> findByAlumno(Alumno alumno);

    List<ObservacionDiaria> findByAlumno_Id(Long alumnoId);

    List<ObservacionDiaria> findByFecha(LocalDate fecha);

    List<ObservacionDiaria> findByFechaBetween(LocalDate inicio, LocalDate fin);

    // 💡 Método para contar el número de observaciones diarias de un alumno.
    Long countByAlumnoId(Long alumnoId);

    // Puedes añadir otros métodos específicos si los necesitas, como:
    // List<ObservacionDiaria> findByAlumnoIdOrderByFechaCreacionDesc(Long alumnoId);

    // =============================
    // 🔹 RELACIONES / FETCH
    // =============================

    /**
     * Recupera una observación junto con el alumno asociado (fetch join para evitar N+1).
     */
    @Query("SELECT od FROM ObservacionDiaria od " +
           "LEFT JOIN FETCH od.alumno " +
           "WHERE od.id = :id")
    Optional<ObservacionDiaria> findByIdWithAlumno(@Param("id") Long id);

    /**
     * Obtiene todas las observaciones de un alumno con la relación cargada.
     */
    @Query("SELECT od FROM ObservacionDiaria od " +
           "LEFT JOIN FETCH od.alumno " +
           "WHERE od.alumno.id = :alumnoId " +
           "ORDER BY od.fecha DESC")
    List<ObservacionDiaria> findByAlumno_IdWithAlumno(@Param("alumnoId") Long alumnoId);

    // =============================
    // 🔹 AGREGADOS / CONTADORES
    // =============================

    @Query("SELECT COUNT(od) FROM ObservacionDiaria od WHERE od.alumno.id = :alumnoId")
    Long countByAlumno(@Param("alumnoId") Long alumnoId);

    @Query("SELECT SUM(od.horasRealizadas) FROM ObservacionDiaria od WHERE od.alumno.id = :alumnoId")
    Integer sumHorasRealizadasByAlumno(@Param("alumnoId") Long alumnoId);

    @Query("SELECT COUNT(od) FROM ObservacionDiaria od WHERE od.fecha BETWEEN :inicio AND :fin")
    Long countByRangoFechas(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
