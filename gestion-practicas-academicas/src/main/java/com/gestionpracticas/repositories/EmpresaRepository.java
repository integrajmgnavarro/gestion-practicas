package com.gestionpracticas.repositories;

import com.gestionpracticas.models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    /**
     * Busca una empresa por su CIF para garantizar la unicidad.
     */
    Optional<Empresa> findByCif(String cif);

    /**
     * Busca empresas por estado (activo/inactivo).
     */
    List<Empresa> findByActivo(Boolean activo);

    /**
     * Verifica si existen alumnos asociados a esta empresa.
     * Crucial para la regla de negocio de eliminación.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM Alumno a WHERE a.empresa.id = :empresaId")
    boolean existsByAlumnosIsNotEmpty(@Param("empresaId") Long empresaId);

    /**
     * Verifica si existen tutores de prácticas asociados a esta empresa.
     * Crucial para la regla de negocio de eliminación.
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END FROM TutorPracticas t WHERE t.empresa.id = :empresaId")
    boolean existsByTutoresPracticasIsNotEmpty(@Param("empresaId") Long empresaId);
}
