package com.gestionpracticas.repositories;

import com.gestionpracticas.models.TutorCurso;
import com.gestionpracticas.models.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorCursoRepository extends JpaRepository<TutorCurso, Long> {

    // Búsquedas básicas
    Optional<TutorCurso> findByDni(String dni);
    Optional<TutorCurso> findByEmail(String email);
    Optional<TutorCurso> findByUsuarioId(Long usuarioId);

    // Verificaciones de existencia
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);

    // Búsquedas por relaciones
    List<TutorCurso> findByCurso(Curso curso);
    List<TutorCurso> findByCursoId(Long cursoId);

    List<TutorCurso> findByEspecialidad(String especialidad);
    List<TutorCurso> findByEspecialidadContainingIgnoreCase(String especialidad);
    
    // Búsquedas por estado
    List<TutorCurso> findByActivo(Boolean activo);

    // Consultas personalizadas con JPQL
    @Query("SELECT t FROM TutorCurso t WHERE " +
            "(:nombre IS NULL OR LOWER(t.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:apellidos IS NULL OR LOWER(t.apellidos) LIKE LOWER(CONCAT('%', :apellidos, '%'))) AND " +
            "(:dni IS NULL OR t.dni = :dni) AND " +
            "(:email IS NULL OR LOWER(t.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:activo IS NULL OR t.activo = :activo)")
     List<TutorCurso> findByMultipleCriteria(
             @Param("nombre") String nombre,
             @Param("apellidos") String apellidos,
             @Param("dni") String dni,
             @Param("email") String email,
             @Param("activo") Boolean activo
     );
    
    // Número de cursos que gestiona cada tutor
    @Query("SELECT COUNT(c) FROM Curso c WHERE c.tutorCurso.id = :tutorId")
    Long countCursosByTutor(@Param("tutorId") Long tutorId);

    // Tutores que actualmente tienen cursos activos
    @Query("SELECT DISTINCT t FROM TutorCurso t JOIN t.cursos c WHERE c.activo = true")
    List<TutorCurso> findTutoresConCursosActivos();

    // Tutores sin cursos asignados
    @Query("SELECT t FROM TutorCurso t WHERE t.cursos IS EMPTY") 
    List<TutorCurso> findTutoresSinCursos();

    // =============================
    // 🔹 ORDENACIONES Y FILTROS
    // =============================

    List<TutorCurso> findAllByOrderByApellidosAsc();
    List<TutorCurso> findAllByOrderByEspecialidadAsc();
}