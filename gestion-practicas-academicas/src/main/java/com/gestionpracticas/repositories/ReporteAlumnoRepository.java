package com.gestionpracticas.repositories;

import com.gestionpracticas.models.ReporteAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad ReporteAlumno.
 * Permite buscar los reportes por el Alumno o por el Tutor del Curso.
 */
@Repository
public interface ReporteAlumnoRepository extends JpaRepository<ReporteAlumno, Long> {

    /**
     * Busca todos los ReportesAlumno dirigidos a un Alumno específico.
     * Esta es la consulta clave que el Alumno usará para "consultar sus reportes".
     * @param alumnoId El ID del Alumno.
     * @return Lista de ReporteAlumno.
     */
    List<ReporteAlumno> findByAlumnoIdOrderByFechaEmisionDesc(Long alumnoId);

    /**
     * Busca todos los ReportesAlumno emitidos por un TutorCurso específico.
     * Esta es la consulta clave que el Tutor del Curso usará para ver los reportes que ha generado.
     * @param tutorCursoId El ID del Tutor del Curso.
     * @return Lista de ReporteAlumno.
     */
    List<ReporteAlumno> findByTutorCursoIdOrderByFechaEmisionDesc(Long tutorCursoId);
    

    /**
     * Busca todos los reportes asociados a un alumno específico a través de la relación.
     * El nombre del método sigue la convención de Spring Data JPA: findBy<Entity>_<Property>.
     * @param alumnoId El ID del alumno.
     * @return Lista de ReporteAlumno.
     */
    List<ReporteAlumno> findByAlumno_Id(Long alumnoId);

    /**
     * Busca todos los reportes asociados a un tutor de curso específico.
     * @param tutorCursoId El ID del tutor de curso.
     * @return Lista de ReporteAlumno.
     */
    List<ReporteAlumno> findByTutorCurso_Id(Long tutorCursoId);
}
