package com.gestionpracticas.repositories;

import com.gestionpracticas.models.TutorCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // IMPORTACIÓN CLAVE
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorCursoRepository extends JpaRepository<TutorCurso, Long>, JpaSpecificationExecutor<TutorCurso> { // EXTENSIÓN CLAVE

    Optional<TutorCurso> findByDni(String dni);
    Optional<TutorCurso> findByEmail(String email);
    List<TutorCurso> findByActivo(Boolean activo);

    /**
     * Verifica si existen alumnos asignados a este tutor.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM Alumno a WHERE a.tutorCurso.id = :tutorId")
    boolean existsByAlumnosIsNotEmpty(@Param("tutorId") Long tutorId);

    /**
     * Verifica si existen cursos asignados a este tutor.
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Curso c WHERE c.tutorCurso.id = :tutorId")
    boolean existsByCursosIsNotEmpty(@Param("tutorId") Long tutorId);
}