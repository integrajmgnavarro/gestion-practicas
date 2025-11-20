package com.gestionpracticas.services;

import com.gestionpracticas.dto.ReporteAlumnoRequestDTO;
import com.gestionpracticas.dto.ReporteAlumnoResponseDTO;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.models.Alumno;
import com.gestionpracticas.models.ReporteAlumno;
import com.gestionpracticas.models.TutorCurso;
import com.gestionpracticas.repositories.AlumnoRepository;
import com.gestionpracticas.repositories.ReporteAlumnoRepository;
import com.gestionpracticas.repositories.TutorCursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la interfaz ReporteAlumnoService.
 * Implementa todos los métodos de la interfaz y corrige errores de mapeo.
 */
@Service
public class ReporteAlumnoServiceImpl implements ReporteAlumnoService {

    private final ReporteAlumnoRepository reporteRepository;
    private final AlumnoRepository alumnoRepository;
    private final TutorCursoRepository tutorCursoRepository;

    // Inyección de dependencias por constructor
    public ReporteAlumnoServiceImpl(ReporteAlumnoRepository reporteRepository,
                                    AlumnoRepository alumnoRepository,
                                    TutorCursoRepository tutorCursoRepository) {
        this.reporteRepository = reporteRepository;
        this.alumnoRepository = alumnoRepository;
        this.tutorCursoRepository = tutorCursoRepository;
    }

    /**
     * Helper para mapear el DTO de Request a la entidad ReporteAlumno.
     */
    private ReporteAlumno mapDtoToEntity(ReporteAlumnoRequestDTO requestDTO, ReporteAlumno reporte) throws ResourceNotFoundException {
        // Carga Alumno
        Alumno alumno = alumnoRepository.findById(requestDTO.getAlumnoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con ID: " + requestDTO.getAlumnoId()));

        // Carga TutorCurso
        TutorCurso tutorCurso = tutorCursoRepository.findById(requestDTO.getTutorCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de Curso no encontrado con ID: " + requestDTO.getTutorCursoId()));

        // Mapeo de campos de contenido y relaciones
        reporte.setAlumno(alumno);
        reporte.setTutorCurso(tutorCurso);
        reporte.setTitulo(requestDTO.getTitulo());
        reporte.setResumenEjecutivo(requestDTO.getResumenEjecutivo());
        reporte.setAspectosDestacados(requestDTO.getAspectosDestacados());
        reporte.setAreasMejora(requestDTO.getAreasMejora());

        // La calificación no se mapea, ya que el campo no existe en la Entidad ReporteAlumno.

        return reporte;
    }

    /**
     * Implementa: ReporteAlumnoService.emitirReporte
     */
    @Override
    @Transactional
    public ReporteAlumnoResponseDTO emitirReporte(ReporteAlumnoRequestDTO requestDTO) throws ResourceNotFoundException {
        ReporteAlumno nuevoReporte = new ReporteAlumno();

        // Mapea los datos relacionales y de contenido
        mapDtoToEntity(requestDTO, nuevoReporte);

        // Establece la fecha de emisión
        nuevoReporte.setFechaEmision(LocalDate.now());

        ReporteAlumno savedReporte = reporteRepository.save(nuevoReporte);
        // Utiliza el DTO estático para el mapeo final
        return ReporteAlumnoResponseDTO.fromEntity(savedReporte);
    }

    /**
     * Implementa: ReporteAlumnoService.obtenerReportesPorAlumno
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReporteAlumnoResponseDTO> obtenerReportesPorAlumno(Long alumnoId) throws ResourceNotFoundException {
        // Primero verifica que el alumno exista
        if (!alumnoRepository.existsById(alumnoId)) {
            throw new ResourceNotFoundException("Alumno no encontrado con ID: " + alumnoId);
        }

        // Usa el método del repositorio que ordena por fecha de emisión descendente
        return reporteRepository.findByAlumnoIdOrderByFechaEmisionDesc(alumnoId).stream()
                .map(ReporteAlumnoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Implementa: ReporteAlumnoService.obtenerReportesPorTutorCurso
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReporteAlumnoResponseDTO> obtenerReportesPorTutorCurso(Long tutorCursoId) {
        // Usa el método del repositorio que ordena por fecha de emisión descendente
        return reporteRepository.findByTutorCursoIdOrderByFechaEmisionDesc(tutorCursoId).stream()
                .map(ReporteAlumnoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}