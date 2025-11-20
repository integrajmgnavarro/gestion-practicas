package com.gestionpracticas.repositories;

import com.gestionpracticas.models.TutorPracticas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <--- NUEVA IMPORTACIÓN
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// Importante: Se añade JpaSpecificationExecutor<TutorPracticas>
public interface TutorPracticasRepository extends JpaRepository<TutorPracticas, Long>, JpaSpecificationExecutor<TutorPracticas> {

    Optional<TutorPracticas> findByDni(String dni);
    Optional<TutorPracticas> findByEmail(String email);
    List<TutorPracticas> findByActivo(Boolean activo);

    /**
     * Nuevo método: Encuentra todos los tutores asociados a una empresa por su ID.
     */
    List<TutorPracticas> findByEmpresa_Id(Long empresaId);

    /**
     * Verifica si existen alumnos asignados a este tutor.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM Alumno a WHERE a.tutorPracticas.id = :tutorId")
    boolean existsByAlumnosIsNotEmpty(@Param("tutorId") Long tutorId);

    /**
     * Verifica si existen incidencias creadas por o asociadas a este tutor.
     */
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END FROM Incidencia i WHERE i.tutorPracticas.id = :tutorId")
    boolean existsByIncidenciasIsNotEmpty(@Param("tutorId") Long tutorId);

    /**
     * Verifica si existen evaluaciones asociadas a este tutor.
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM Evaluacion e WHERE e.tutorPracticas.id = :tutorId")
    boolean existsByEvaluacionesIsNotEmpty(@Param("tutorId") Long tutorId);

    /**
     * Verifica si existen evaluaciones de tutor (EvaluacionTutor) asociadas a este tutor.
     */
    @Query("SELECT CASE WHEN COUNT(et) > 0 THEN TRUE ELSE FALSE END FROM EvaluacionTutor et WHERE et.tutorPracticas.id = :tutorId")
    boolean existsByEvaluacionesTutorIsNotEmpty(@Param("tutorId") Long tutorId);
}