package com.gestionpracticas.repositories;

import com.gestionpracticas.models.CriterioEvaluacion;
import com.gestionpracticas.models.CapacidadEvaluacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CriterioEvaluacionRepository extends JpaRepository<CriterioEvaluacion, Long > {
	
	//Búsquedas básicas
	
	Optional<CriterioEvaluacion> findByNombre(String nombre);
	List<CriterioEvaluacion> findByNombreContainingIgnoreCase(String nombre);
	
	//Relaciones
	
	List<CriterioEvaluacion> findByCapacidades(CapacidadEvaluacion capacidadEvaluacion);
	
	//Filtro de estado
	
	List<CriterioEvaluacion> findByActivo(Boolean activo);
	
	//Busqueda multicriterio
	
	@Query("SELECT cre FROM CriterioEvaluacion cre WHERE " +
			"(:nombre IS NULL OR LOWER(cre.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
			"(:capacidadEvaluacion IS NULL OR cre.capacidadEvaluacion = :capacidadEvaluacion) AND " +
			"(:activo IS NULL OR cre.activo = :activo)")
	List<CriterioEvaluacion> findByMultipleCriteria(
            @Param("nombre") String nombre,
            @Param("capacidadEvaluacion") Long capacidadEvaluacion,
            @Param("activo") Boolean activo
    );	
}
