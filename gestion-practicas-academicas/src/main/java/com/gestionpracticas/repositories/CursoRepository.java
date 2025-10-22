package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Curso;
import com.gestionpracticas.models.TutorCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    // =============================
    // 🔹 BÚSQUEDAS BÁSICAS
    // =============================

    Optional<Curso> findByNombre(String nombre);
    List<Curso> findByNombreContainingIgnoreCase(String nombre);

    // =============================
    // 🔹 RELACIONES
    // =============================

    List<Curso> findByTutorCurso(TutorCurso tutorCurso);
    List<Curso> findByTutorCursoId(Long tutorCursoId);

    // =============================
    // 🔹 FILTROS DE ESTADO
    // =============================

    List<Curso> findByActivo(Boolean activo);
    List<Curso> findByTutorCursoIdAndActivo(Long tutorCursoId, Boolean activo);

    // =============================
    // 🔹 CONSULTAS PERSONALIZADAS
    // =============================

    @Query("SELECT COUNT(c) FROM Curso c WHERE c.activo = true")
    Long countCursosActivos();

    @Query("SELECT COUNT(c) FROM Curso c WHERE c.tutorCurso.id = :tutorCursoId AND c.activo = true")
    Long countCursosActivosByTutor(@Param("tutorCursoId") Long tutorCursoId);

    // =============================
    // 🔹 BÚSQUEDA MULTICRITERIO
    // =============================

    @Query("SELECT c FROM Curso c WHERE " +
           "(:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:tutorCursoId IS NULL OR c.tutorCurso.id = :tutorCursoId) AND " +
           "(:activo IS NULL OR c.activo = :activo)")
    List<Curso> findByMultipleCriteria(
            @Param("nombre") String nombre,
            @Param("tutorCursoId") Long tutorCursoId,
            @Param("activo") Boolean activo
    );
}
