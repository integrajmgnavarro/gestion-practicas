package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Alumno;
import com.gestionpracticas.models.Curso;
import com.gestionpracticas.models.Empresa;
import com.gestionpracticas.models.TutorPracticas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    // Búsquedas básicas
    Optional<Alumno> findByDni(String dni);
    Optional<Alumno> findByEmail(String email);
    Optional<Alumno> findByUsuario_Id(Long usuarioId);

    // Verificaciones de existencia
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);

    // Búsquedas por relaciones
    List<Alumno> findByCurso(Curso curso);
    List<Alumno> findByCurso_Id(Long cursoId);

    List<Alumno> findByEmpresa(Empresa empresa);
    List<Alumno> findByEmpresa_Id(Long empresaId);

    List<Alumno> findByTutorPracticas(TutorPracticas tutorPracticas);
    List<Alumno> findByTutorPracticas_Id(Long tutorPracticasId);

    // Búsquedas por estado
    List<Alumno> findByActivo(Boolean activo);
    List<Alumno> findByFechaFinIsNotNull();
    List<Alumno> findByDuracionPracticasIsNotNull();

    // Búsquedas por fechas
    List<Alumno> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);
    List<Alumno> findByFechaFinBefore(LocalDate fecha);
    List<Alumno> findByFechaFinAfter(LocalDate fecha);

    // Búsquedas combinadas
    List<Alumno> findByCurso_IdAndActivo(Long cursoId, Boolean activo);
    List<Alumno> findByEmpresa_IdAndActivo(Long empresaId, Boolean activo);
    List<Alumno> findByTutorPracticas_IdAndActivo(Long tutorPracticasId, Boolean activo);

    // Consultas personalizadas con JPQL
    @Query("SELECT a FROM Alumno a WHERE a.curso.tutorCurso.id = :tutorCursoId")
    List<Alumno> findByTutorCurso_Id(@Param("tutorCursoId") Long tutorCursoId);

    @Query("SELECT a FROM Alumno a WHERE a.activo = true AND a.fechaFin >= CURRENT_DATE")
    List<Alumno> findAlumnosConPracticasActivas();

    @Query("SELECT a FROM Alumno a WHERE a.activo = true AND a.fechaFin < CURRENT_DATE")
    List<Alumno> findAlumnosConPracticasFinalizadas();

    @Query("SELECT COUNT(a) FROM Alumno a WHERE a.curso.id = :cursoId AND a.activo = true")
    Long countAlumnosActivosByCurso(@Param("cursoId") Long cursoId);

    @Query("SELECT COUNT(a) FROM Alumno a WHERE a.empresa.id = :empresaId AND a.activo = true")
    Long countAlumnosActivosByEmpresa(@Param("empresaId") Long empresaId);

    // --- NUEVO MÉTODO AÑADIDO PARA LA BÚSQUEDA GENERAL ---
    /**
     * Busca alumnos por coincidencia en nombre, apellidos, DNI o email.
     * Es la implementación que el AlumnoService necesita para searchAlumnos.
     */
    @Query("SELECT a FROM Alumno a WHERE " +
            "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.apellidos) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.dni) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Alumno> findBySearchTerm(@Param("searchTerm") String searchTerm);
    // ----------------------------------------------------

    // Búsqueda con múltiples criterios (sin paginación)
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

    // Búsqueda con múltiples criterios y PAGINACIÓN (CORRECCIÓN: DNI usa coincidencia exacta)
    @Query("SELECT a FROM Alumno a " +
            "WHERE (:nombre IS NULL OR lower(a.nombre) LIKE lower(concat('%', :nombre, '%'))) " +
            "AND (:apellidos IS NULL OR lower(a.apellidos) LIKE lower(concat('%', :apellidos, '%'))) " +
            "AND (:dni IS NULL OR a.dni = :dni) " + // <-- FIX IMPORTANTE: DNI usa coincidencia exacta
            "AND (:cursoId IS NULL OR a.curso.id = :cursoId) " +
            "AND (:empresaId IS NULL OR a.empresa.id = :empresaId) " +
            "AND (:activo IS NULL OR a.activo = :activo)")
      Page<Alumno> findByMultipleCriteriaWithPagination( // <-- Nombre usado en AlumnoService
            @Param("nombre") String nombre,
            @Param("apellidos") String apellidos,
            @Param("dni") String dni,
            @Param("cursoId") Long cursoId,
            @Param("empresaId") Long empresaId,
            @Param("activo") Boolean activo,
            Pageable pageable);
}