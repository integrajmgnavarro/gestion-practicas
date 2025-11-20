package com.gestionpracticas.services;

import com.gestionpracticas.dto.IncidenciaCreateDTO;
import com.gestionpracticas.dto.IncidenciaDTO;
import com.gestionpracticas.models.Incidencia;
import com.gestionpracticas.repositories.IncidenciaRepository;
import com.gestionpracticas.repositories.AlumnoRepository;
import com.gestionpracticas.repositories.TutorPracticasRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidenciaServiceImpl implements IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final AlumnoRepository alumnoRepository;
    private final TutorPracticasRepository tutorPracticasRepository;

    // ==========================================================
    // MÉTODOS PÚBLICOS DEL SERVICIO
    // ==========================================================

    /**
     * Recupera una incidencia por su ID, lanzando una excepción si no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public IncidenciaDTO getIncidenciaById(Long id) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incidencia con ID " + id + " no encontrada."));
        return mapEntityToDto(incidencia);
    }

    @Override
    @Transactional
    public IncidenciaDTO createIncidencia(IncidenciaCreateDTO createDTO) {
        // 1. Cargar las entidades relacionadas
        var alumno = alumnoRepository.findById(createDTO.getAlumnoId())
                .orElseThrow(() -> new EntityNotFoundException("Alumno con ID " + createDTO.getAlumnoId() + " no encontrado."));

        var tutor = tutorPracticasRepository.findById(createDTO.getTutorPracticasId())
                .orElseThrow(() -> new EntityNotFoundException("Tutor de Prácticas con ID " + createDTO.getTutorPracticasId() + " no encontrado."));

        // 2. Mapear DTO a entidad (Mapeo manual en el service)
        Incidencia incidencia = mapCreateDtoToEntity(createDTO);

        // 3. Asignar las entidades
        incidencia.setAlumno(alumno);
        incidencia.setTutorPracticas(tutor);
        
        // 4. Guardar la entidad
        Incidencia savedIncidencia = incidenciaRepository.save(incidencia);

        // 5. Mapear de vuelta a DTO y devolver
        return mapEntityToDto(savedIncidencia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidenciaDTO> getIncidenciasByAlumnoIds(List<Long> alumnoIds) {
        // Asumiendo que findByAlumno_IdIn existe en el repositorio
        List<Incidencia> incidencias = incidenciaRepository.findByAlumno_IdIn(alumnoIds);
        return mapEntityListToDtoList(incidencias);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidenciaDTO> getIncidenciasByAlumnoId(Long alumnoId) {
        // Usamos el método optimizado del repositorio
        List<Incidencia> incidencias = incidenciaRepository.findByAlumno_IdWithRelations(alumnoId);
        return mapEntityListToDtoList(incidencias);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidenciaDTO> getIncidenciasByTutorPracticasId(Long tutorPracticasId) {
        var tutor = tutorPracticasRepository.findById(tutorPracticasId)
                .orElseThrow(() -> new EntityNotFoundException("Tutor de Prácticas con ID " + tutorPracticasId + " no encontrado."));
        
        // Usamos el método por relación del repositorio
        List<Incidencia> incidencias = incidenciaRepository.findByTutorPracticas(tutor);
        return mapEntityListToDtoList(incidencias);
    }

    // ==========================================================
    // MÉTODOS PRIVADOS DE MAPEO (SOLUCIÓN INTERNA SIN CONVERTERS)
    // ==========================================================

    private IncidenciaDTO mapEntityToDto(Incidencia incidencia) {
        if (incidencia == null) return null;

        IncidenciaDTO dto = new IncidenciaDTO();
        dto.setId(incidencia.getId());
        dto.setFecha(incidencia.getFecha());
        dto.setTipo(incidencia.getTipo());
        dto.setDescripcion(incidencia.getDescripcion());
        dto.setResolucion(incidencia.getResolucion());
        dto.setEstado(incidencia.getEstado());
        dto.setFechaCreacion(incidencia.getFechaCreacion());
        dto.setFechaResolucion(incidencia.getFechaResolucion());

        // Mapeo de IDs de relaciones
        if (incidencia.getAlumno() != null) {
            dto.setAlumnoId(incidencia.getAlumno().getId());
        }
        if (incidencia.getTutorPracticas() != null) {
            dto.setTutorPracticasId(incidencia.getTutorPracticas().getId());
        }
        return dto;
    }

    private List<IncidenciaDTO> mapEntityListToDtoList(List<Incidencia> incidencias) {
        return incidencias.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    private Incidencia mapCreateDtoToEntity(IncidenciaCreateDTO createDTO) {
        Incidencia incidencia = new Incidencia();
        incidencia.setFecha(createDTO.getFecha());
        incidencia.setTipo(createDTO.getTipo());
        incidencia.setDescripcion(createDTO.getDescripcion());
        
        // Asignar valores por defecto para una nueva incidencia
        incidencia.setEstado(Incidencia.EstadoIncidencia.ABIERTA);
        // Nota: La fechaCreacion debería inicializarse en el modelo (Incidencia.java)
        
        return incidencia;
    }
}
