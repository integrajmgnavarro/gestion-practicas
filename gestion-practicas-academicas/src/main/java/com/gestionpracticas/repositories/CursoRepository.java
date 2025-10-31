package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    /**
     * Verifica si ya existe un curso con el código especificado.
     */
    boolean existsByCodigo(String codigo);

    /**
     * Busca un curso por su código. Útil para validación en la actualización.
     */
    Optional<Curso> findByCodigo(String codigo);
    
    /**
     * Busca cursos por ID de Tutor de Curso.
     */
    List<Curso> findByTutorCurso_Id(Long tutorCursoId);

    /**
     * Verifica si el curso tiene alumnos asociados para prevenir la eliminación.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM Alumno a WHERE a.curso.id = :cursoId")
    boolean hasAlumnos(@Param("cursoId") Long cursoId);

}
