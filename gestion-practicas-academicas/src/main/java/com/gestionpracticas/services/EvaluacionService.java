package com.gestionpracticas.services;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.exception.ResourceNotFoundException;
import java.math.RoundingMode;
import com.gestionpracticas.models.*;
import com.gestionpracticas.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluacionService {

    private final CriterioEvaluacionRepository criterioEvaluacionRepository;
    private final CapacidadEvaluacionRepository capacidadEvaluacionRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionTutorRepository evaluacionTutorRepository;
    private final AlumnoRepository alumnoRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final TutorCursoRepository tutorCursoRepository;

    // ========================= CRITERIOS DE EVALUACIÓN ========================= //

    @Transactional
    public CriterioEvaluacionDTO createCriterioEvaluacion(CriterioEvaluacionCreateDTO dto) {
        CriterioEvaluacion criterio = new CriterioEvaluacion();
        criterio.setNombre(dto.getNombre());
        criterio.setDescripcion(dto.getDescripcion());
        criterio.setPeso(dto.getPeso());
        criterio.setActivo(true);

        criterio = criterioEvaluacionRepository.save(criterio);
        return convertToDTO(criterio);
    }

    @Transactional(readOnly = true)
    public CriterioEvaluacionDTO getCriterioEvaluacionById(Long id) {
        CriterioEvaluacion criterio = criterioEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Criterio de evaluación no encontrado con id: " + id));
        return convertToDTO(criterio);
    }

    @Transactional(readOnly = true)
    public List<CriterioEvaluacionDTO> getAllCriteriosEvaluacion() {
        return criterioEvaluacionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CriterioEvaluacionDTO> getCriteriosEvaluacionActivos() {
        return criterioEvaluacionRepository.findByActivo(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CriterioEvaluacionDTO updateCriterioEvaluacion(Long id, CriterioEvaluacionUpdateDTO dto) {
        CriterioEvaluacion criterio = criterioEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Criterio de evaluación no encontrado"));

        if (dto.getNombre() != null) criterio.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) criterio.setDescripcion(dto.getDescripcion());
        if (dto.getPeso() != null) criterio.setPeso(dto.getPeso());
        if (dto.getActivo() != null) criterio.setActivo(dto.getActivo());

        criterio = criterioEvaluacionRepository.save(criterio);
        return convertToDTO(criterio);
    }

    @Transactional
    public void deleteCriterioEvaluacion(Long id) {
        CriterioEvaluacion criterio = criterioEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Criterio de evaluación no encontrado"));
        criterioEvaluacionRepository.delete(criterio);
    }

    // ========================= CAPACIDADES DE EVALUACIÓN ========================= //

    @Transactional
    public CapacidadEvaluacionDTO createCapacidadEvaluacion(CapacidadEvaluacionCreateDTO dto) {
        CriterioEvaluacion criterio = criterioEvaluacionRepository.findById(dto.getCriterioId())
                .orElseThrow(() -> new ResourceNotFoundException("Criterio de evaluación no encontrado con id: " + dto.getCriterioId()));

        CapacidadEvaluacion capacidad = new CapacidadEvaluacion();
        capacidad.setNombre(dto.getNombre());
        capacidad.setDescripcion(dto.getDescripcion());
        capacidad.setPuntuacionMaxima(dto.getPuntuacionMaxima() != null ? dto.getPuntuacionMaxima() : 10);
        capacidad.setCriterio(criterio);
        capacidad.setActivo(true);

        capacidad = capacidadEvaluacionRepository.save(capacidad);
        return convertToDTO(capacidad);
    }

    @Transactional(readOnly = true)
    public CapacidadEvaluacionDTO getCapacidadEvaluacionById(Long id) {
        CapacidadEvaluacion capacidad = capacidadEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Capacidad de evaluación no encontrada con id: " + id));
        return convertToDTO(capacidad);
    }

    @Transactional(readOnly = true)
    public List<CapacidadEvaluacionDTO> getAllCapacidadesEvaluacion() {
        return capacidadEvaluacionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CapacidadEvaluacionDTO> getCapacidadesByCriterioId(Long criterioId) {
        return capacidadEvaluacionRepository.findByCriterio_Id(criterioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CapacidadEvaluacionDTO> getCapacidadesEvaluacionActivas() {
        return capacidadEvaluacionRepository.findByActivoTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CapacidadEvaluacionDTO updateCapacidadEvaluacion(Long id, CapacidadEvaluacionUpdateDTO dto) {
        CapacidadEvaluacion capacidad = capacidadEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Capacidad de evaluación no encontrada"));

        if (dto.getNombre() != null) capacidad.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) capacidad.setDescripcion(dto.getDescripcion());
        if (dto.getPuntuacionMaxima() != null) capacidad.setPuntuacionMaxima(dto.getPuntuacionMaxima());
        if (dto.getActivo() != null) capacidad.setActivo(dto.getActivo());

        capacidad = capacidadEvaluacionRepository.save(capacidad);
        return convertToDTO(capacidad);
    }

    @Transactional
    public void deleteCapacidadEvaluacion(Long id) {
        CapacidadEvaluacion capacidad = capacidadEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Capacidad de evaluación no encontrada"));
        capacidadEvaluacionRepository.delete(capacidad);
    }

    // ========================= EVALUACIONES DE ALUMNOS ========================= //

    @Transactional
    public EvaluacionDTO createEvaluacion(EvaluacionCreateDTO dto) {
        Alumno alumno = alumnoRepository.findById(dto.getAlumnoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id: " + dto.getAlumnoId()));

        TutorPracticas tutorPracticas = tutorPracticasRepository.findById(dto.getTutorPracticasId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de prácticas no encontrado con id: " + dto.getTutorPracticasId()));

        CapacidadEvaluacion capacidad = capacidadEvaluacionRepository.findById(dto.getCapacidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Capacidad de evaluación no encontrada con id: " + dto.getCapacidadId()));

        // Validar que la puntuación no exceda la puntuación máxima
        if (dto.getPuntuacion().compareTo(BigDecimal.valueOf(capacidad.getPuntuacionMaxima())) > 0) {
            throw new IllegalArgumentException("La puntuación no puede exceder la puntuación máxima de " + capacidad.getPuntuacionMaxima());
        }

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setAlumno(alumno);
        evaluacion.setTutorPracticas(tutorPracticas);
        evaluacion.setCapacidad(capacidad);
        evaluacion.setPuntuacion(dto.getPuntuacion());
        evaluacion.setObservaciones(dto.getObservaciones());
        evaluacion.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());

        evaluacion = evaluacionRepository.save(evaluacion);
        return convertToDTO(evaluacion);
    }

    @Transactional(readOnly = true)
    public EvaluacionDTO getEvaluacionById(Long id) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con id: " + id));
        return convertToDTO(evaluacion);
    }

    @Transactional(readOnly = true)
    public List<EvaluacionDTO> getAllEvaluaciones() {
        return evaluacionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EvaluacionDTO> getEvaluacionesByAlumnoId(Long alumnoId) {
        return evaluacionRepository.findByAlumno_Id(alumnoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EvaluacionDTO> getEvaluacionesByTutorPracticasId(Long tutorId) {
        return evaluacionRepository.findByTutorPracticas_Id(tutorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Implementación necesaria para el controlador AlumnoWebController.
     * Alias del método calcularNotaFinalAlumno.
     * @param alumnoId ID del alumno.
     * @return La nota final ponderada.
     */
    @Transactional(readOnly = true)
    public Double calcularNotaMedia(Long alumnoId) {
        // El controlador espera un Double, convertimos el BigDecimal a Double.
        return calcularNotaFinalAlumno(alumnoId).doubleValue();
    }
    
    /**
     * Cuenta el número total de evaluaciones registradas para un alumno.
     * Este método reemplaza a alumnoService.contarEvaluaciones(alumnoId).
     * @param alumnoId ID del alumno.
     * @return Número total de evaluaciones.
     */
    @Transactional(readOnly = true)
    public Long contarEvaluaciones(Long alumnoId) {
        return evaluacionRepository.countByAlumnoId(alumnoId);
    }

    /**
     * Calcula la nota final del alumno ponderada por los criterios de evaluación.
     * Este método reemplaza a alumnoService.calcularNotaMedia(alumnoId).
     * @param alumnoId ID del alumno.
     * @return La nota final ponderada.
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularNotaFinalAlumno(Long alumnoId) {
        List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumno_Id(alumnoId);
        
        if (evaluaciones.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Calcular nota ponderada por criterio
        BigDecimal notaTotal = BigDecimal.ZERO;
        BigDecimal pesoTotal = BigDecimal.ZERO;

        for (Evaluacion eval : evaluaciones) {
            CriterioEvaluacion criterio = eval.getCapacidad().getCriterio();
            BigDecimal peso = criterio.getPeso();
            // Normalizar a base 10 (ej: 8/10 -> 8.0)
            BigDecimal puntuacionNormalizada = eval.getPuntuacion()
            		.divide(BigDecimal.valueOf(eval.getCapacidad().getPuntuacionMaxima()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.TEN);
            
            // notaTotal += (puntuacionNormalizada * peso) / 100
            notaTotal = notaTotal.add(puntuacionNormalizada.multiply(peso).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            pesoTotal = pesoTotal.add(peso);
        }

        if (pesoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return notaTotal.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public EvaluacionDTO updateEvaluacion(Long id, EvaluacionUpdateDTO dto) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        if (dto.getPuntuacion() != null) {
            // Validar que la puntuación no exceda la puntuación máxima
            if (dto.getPuntuacion().compareTo(BigDecimal.valueOf(evaluacion.getCapacidad().getPuntuacionMaxima())) > 0) {
                throw new IllegalArgumentException("La puntuación no puede exceder la puntuación máxima de " + evaluacion.getCapacidad().getPuntuacionMaxima());
            }
            evaluacion.setPuntuacion(dto.getPuntuacion());
        }
        if (dto.getObservaciones() != null) evaluacion.setObservaciones(dto.getObservaciones());
        if (dto.getFecha() != null) evaluacion.setFecha(dto.getFecha());

        evaluacion = evaluacionRepository.save(evaluacion);
        return convertToDTO(evaluacion);
    }

    @Transactional
    public void deleteEvaluacion(Long id) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));
        evaluacionRepository.delete(evaluacion);
    }

    // ========================= EVALUACIONES DE TUTORES ========================= //

    @Transactional
    public EvaluacionTutorDTO createEvaluacionTutor(EvaluacionTutorCreateDTO dto) {
        TutorPracticas tutorPracticas = tutorPracticasRepository.findById(dto.getTutorPracticasId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de prácticas no encontrado con id: " + dto.getTutorPracticasId()));

        TutorCurso tutorCurso = tutorCursoRepository.findById(dto.getTutorCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de curso no encontrado con id: " + dto.getTutorCursoId()));

        EvaluacionTutor evaluacionTutor = new EvaluacionTutor();
        evaluacionTutor.setTutorPracticas(tutorPracticas);
        evaluacionTutor.setTutorCurso(tutorCurso);
        evaluacionTutor.setPuntuacion(dto.getPuntuacion());
        evaluacionTutor.setObservaciones(dto.getObservaciones());
        evaluacionTutor.setAspectosPositivos(dto.getAspectosPositivos());
        evaluacionTutor.setAspectosMejorar(dto.getAspectosMejorar());
        evaluacionTutor.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());

        evaluacionTutor = evaluacionTutorRepository.save(evaluacionTutor);
        return convertToDTO(evaluacionTutor);
    }

    @Transactional(readOnly = true)
    public EvaluacionTutorDTO getEvaluacionTutorById(Long id) {
        EvaluacionTutor evaluacionTutor = evaluacionTutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación de tutor no encontrada con id: " + id));
        return convertToDTO(evaluacionTutor);
    }

    @Transactional(readOnly = true)
    public List<EvaluacionTutorDTO> getAllEvaluacionesTutor() {
        return evaluacionTutorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EvaluacionTutorDTO> getEvaluacionesTutorByTutorPracticasId(Long tutorPracticasId) {
        return evaluacionTutorRepository.findByTutorPracticas(
            tutorPracticasRepository.findById(tutorPracticasId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado"))
        ).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EvaluacionTutorDTO> getEvaluacionesTutorByTutorCursoId(Long tutorCursoId) {
        TutorCurso tutor = tutorCursoRepository.findById(tutorCursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de curso no encontrado"));
        return evaluacionTutorRepository.findByTutorCurso(tutor).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EvaluacionTutorDTO updateEvaluacionTutor(Long id, EvaluacionTutorUpdateDTO dto) {
        EvaluacionTutor evaluacionTutor = evaluacionTutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación de tutor no encontrada"));

        if (dto.getPuntuacion() != null) evaluacionTutor.setPuntuacion(dto.getPuntuacion());
        if (dto.getObservaciones() != null) evaluacionTutor.setObservaciones(dto.getObservaciones());
        if (dto.getAspectosPositivos() != null) evaluacionTutor.setAspectosPositivos(dto.getAspectosPositivos());
        if (dto.getAspectosMejorar() != null) evaluacionTutor.setAspectosMejorar(dto.getAspectosMejorar());
        if (dto.getFecha() != null) evaluacionTutor.setFecha(dto.getFecha());

        evaluacionTutor = evaluacionTutorRepository.save(evaluacionTutor);
        return convertToDTO(evaluacionTutor);
    }

    @Transactional
    public void deleteEvaluacionTutor(Long id) {
        EvaluacionTutor evaluacionTutor = evaluacionTutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación de tutor no encontrada"));
        evaluacionTutorRepository.delete(evaluacionTutor);
    }

    // ========================= MÉTODOS DE CONVERSIÓN ========================= //

    private CriterioEvaluacionDTO convertToDTO(CriterioEvaluacion c) {
        CriterioEvaluacionDTO dto = new CriterioEvaluacionDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setDescripcion(c.getDescripcion());
        dto.setPeso(c.getPeso());
        dto.setActivo(c.getActivo());
        return dto;
    }

    private CapacidadEvaluacionDTO convertToDTO(CapacidadEvaluacion c) {
        CapacidadEvaluacionDTO dto = new CapacidadEvaluacionDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setDescripcion(c.getDescripcion());
        dto.setPuntuacionMaxima(c.getPuntuacionMaxima());
        dto.setActivo(c.getActivo());
        if (c.getCriterio() != null) {
            dto.setCriterioId(c.getCriterio().getId());
            dto.setCriterioNombre(c.getCriterio().getNombre());
        }
        return dto;
    }

    private EvaluacionDTO convertToDTO(Evaluacion e) {
        EvaluacionDTO dto = new EvaluacionDTO();
        dto.setId(e.getId());
        dto.setPuntuacion(e.getPuntuacion());
        dto.setObservaciones(e.getObservaciones());
        dto.setFecha(e.getFecha());
        dto.setFechaCreacion(e.getFechaCreacion());
        
        if (e.getAlumno() != null) {
            dto.setAlumnoId(e.getAlumno().getId());
            dto.setAlumnoNombre(e.getAlumno().getNombre() + " " + e.getAlumno().getApellidos());
        }
        
        if (e.getTutorPracticas() != null) {
            dto.setTutorPracticasId(e.getTutorPracticas().getId());
            dto.setTutorPracticasNombre(e.getTutorPracticas().getNombre() + " " + e.getTutorPracticas().getApellidos());
        }
        
        if (e.getCapacidad() != null) {
            dto.setCapacidadId(e.getCapacidad().getId());
            dto.setCapacidadNombre(e.getCapacidad().getNombre());
            dto.setPuntuacionMaxima(e.getCapacidad().getPuntuacionMaxima());
            if (e.getCapacidad().getCriterio() != null) {
                dto.setCriterioNombre(e.getCapacidad().getCriterio().getNombre());
            }
        }
        
        return dto;
    }

    private EvaluacionTutorDTO convertToDTO(EvaluacionTutor e) {
        EvaluacionTutorDTO dto = new EvaluacionTutorDTO();
        dto.setId(e.getId());
        dto.setPuntuacion(e.getPuntuacion());
        dto.setObservaciones(e.getObservaciones());
        dto.setAspectosPositivos(e.getAspectosPositivos());
        dto.setAspectosMejorar(e.getAspectosMejorar());
        dto.setFecha(e.getFecha());
        
        if (e.getTutorPracticas() != null) {
            dto.setTutorPracticasId(e.getTutorPracticas().getId());
            dto.setTutorPracticasNombre(e.getTutorPracticas().getNombre() + " " + e.getTutorPracticas().getApellidos());
        }
        
        if (e.getTutorCurso() != null) {
            dto.setTutorCursoId(e.getTutorCurso().getId());
            dto.setTutorCursoNombre(e.getTutorCurso().getNombre() + " " + e.getTutorCurso().getApellidos());
        }
        
        return dto;
    }
}
