package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Alumno;
import com.gestionpracticas.models.Curso;
import com.gestionpracticas.models.Empresa;
import com.gestionpracticas.models.TutorPracticas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    // Búsquedas básicas
    Optional<Alumno> findByDni(String dni);
    Optional<Alumno> findByEmail(String email);
    Optional<Alumno> findByUsuarioId(Long usuarioId);

    // Verificaciones de existencia
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);

    // Búsquedas por relaciones
    List<Alumno> findByCurso(Curso curso);
    List<Alumno> findByCursoId(Long cursoId);

    List<Alumno> findByEmpresa(Empresa empresa);
    List<Alumno> findByEmpresaId(Long empresaId);

    List<Alumno> findByTutorPracticas(TutorPracticas tutorPracticas);
    List<Alumno> findByTutorPracticasId(Long tutorPracticasId);

    // Búsquedas por estado
    List<Alumno> findByActivo(Boolean activo);

    // Búsquedas por fechas
    List<Alumno> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);
    List<Alumno> findByFechaFinBefore(LocalDate fecha);
    List<Alumno> findByFechaFinAfter(LocalDate fecha);

    // Búsquedas combinadas
    List<Alumno> findByCursoIdAndActivo(Long cursoId, Boolean activo);
    List<Alumno> findByEmpresaIdAndActivo(Long empresaId, Boolean activo);
    List<Alumno> findByTutorPracticasIdAndActivo(Long tutorPracticasId, Boolean activo);

    // Consultas personalizadas con JPQL
    @Query("SELECT a FROM Alumno a WHERE a.curso.tutorCurso.id = :tutorCursoId")
    List<Alumno> findByTutorCursoId(@Param("tutorCursoId") Long tutorCursoId);

    @Query("SELECT a FROM Alumno a WHERE a.activo = true AND a.fechaFin >= CURRENT_DATE")
    List<Alumno> findAlumnosConPracticasActivas();

    @Query("SELECT a FROM Alumno a WHERE a.activo = true AND a.fechaFin < CURRENT_DATE")
    List<Alumno> findAlumnosConPracticasFinalizadas();

    @Query("SELECT COUNT(a) FROM Alumno a WHERE a.curso.id = :cursoId AND a.activo = true")
    Long countAlumnosActivosByCurso(@Param("cursoId") Long cursoId);

    @Query("SELECT COUNT(a) FROM Alumno a WHERE a.empresa.id = :empresaId AND a.activo = true")
    Long countAlumnosActivosByEmpresa(@Param("empresaId") Long empresaId);

    // Búsqueda con múltiples criterios
    @Query("SELECT a FROM Alumno a WHERE " +
           "(:nombre IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:apellidos IS NULL OR LOWER(a.apellidos) LIKE LOWER(CONCAT('%', :apellidos, '%'))) AND " +
           "(:dni IS NULL OR a.dni = :dni) AND " +
           "(:cursoId IS NULL OR a.curso.id = :cursoId) AND " +
           "(:empresaId IS NULL OR a.empresa.id = :empresaId) AND " +
           "(:activo IS NULL OR a.activo = :activo)")
    List<Alumno> findByMultipleCriteria(
            @Param("nombre") String nombre,
            @Param("apellidos") String apellidos,
            @Param("dni") String dni,
            @Param("cursoId") Long cursoId,
            @Param("empresaId") Long empresaId,
            @Param("activo") Boolean activo
    );
}
