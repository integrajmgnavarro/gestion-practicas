package com.gestionpracticas.services;

import com.gestionpracticas.dto.ObservacionDiariaCreateDTO;
import com.gestionpracticas.dto.ObservacionDiariaDTO;
import com.gestionpracticas.dto.ObservacionDiariaUpdateDTO;
import com.gestionpracticas.models.Alumno;
import com.gestionpracticas.models.ObservacionDiaria;
import com.gestionpracticas.repositories.AlumnoRepository;
import com.gestionpracticas.repositories.ObservacionDiariaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de Observaciones Diarias.
 * Se encarga de la lógica de negocio y la conversión de/a DTOs.
 */
@Service
public class ObservacionDiariaService {

    @Autowired
    private ObservacionDiariaRepository observacionDiariaRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;
    
    // =============================================
    // 🔹 Funciones de Mapeo Internas
    // =============================================

    /**
     * Convierte una entidad ObservacionDiaria a su DTO de lectura.
     */
    private ObservacionDiariaDTO toDTO(ObservacionDiaria entity) {
        if (entity == null) return null;

        ObservacionDiariaDTO dto = new ObservacionDiariaDTO();
        dto.setId(entity.getId());
        dto.setFecha(entity.getFecha());
        dto.setActividades(entity.getActividades());
        dto.setExplicaciones(entity.getExplicaciones());
        dto.setObservacionesAlumno(entity.getObservacionesAlumno());
        dto.setObservacionesTutor(entity.getObservacionesTutor());
        dto.setHorasRealizadas(entity.getHorasRealizadas());
        dto.setFechaCreacion(entity.getFechaCreacion());

        if (entity.getAlumno() != null) {
            dto.setAlumnoId(entity.getAlumno().getId());
            // Asumiendo que tenemos los campos de nombre/apellido en la entidad Alumno
            String nombreCompleto = String.format("%s %s", entity.getAlumno().getNombre(), entity.getAlumno().getApellidos());
            dto.setNombreAlumno(nombreCompleto);
        }

        return dto;
    }
    
    private List<ObservacionDiariaDTO> toDTOList(List<ObservacionDiaria> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // =============================================
    // 🔹 Lógica Específica del AlumnoWebController
    // =============================================

    /**
     * Recupera todas las observaciones diarias de un alumno específico.
     * Requerido por AlumnoWebController (o un controlador similar).
     * @param alumnoId El ID del alumno.
     * @return Lista de ObservacionDiariaDTOs.
     */
    @Transactional(readOnly = true)
    public List<ObservacionDiariaDTO> getObservacionesByAlumnoId(Long alumnoId) {
        // Se asume el método findByAlumno_Id(Long) en el repositorio
        return toDTOList(observacionDiariaRepository.findByAlumno_Id(alumnoId));
    }
    
    /**
     * Recupera todas las observaciones diarias que corresponden a una lista de IDs de alumnos.
     * ESTE ES EL MÉTODO FALTANTE. Requerido por TutorPracticasController.
     * @param alumnoIds La lista de IDs de alumnos.
     * @return Lista de ObservacionDiariaDTOs.
     */
    @Transactional(readOnly = true)
    public List<ObservacionDiariaDTO> getObservacionesByAlumnoIds(List<Long> alumnoIds) {
        // Se asume el método findByAlumno_IdIn(Collection<Long>) en el repositorio
        return toDTOList(observacionDiariaRepository.findByAlumno_IdIn(alumnoIds));
    }

    /**
     * Cuenta el número total de observaciones diarias de un alumno.
     * Requerido por AlumnoWebController (o un controlador similar).
     * @param alumnoId El ID del alumno.
     * @return El número total de observaciones.
     */
    @Transactional(readOnly = true)
    public Long contarObservaciones(Long alumnoId) {
        return observacionDiariaRepository.countByAlumnoId(alumnoId);
    }
    
    // =============================================
    // 🔹 CRUD Básico
    // =============================================

    /**
     * Crea una nueva Observación Diaria a partir de un DTO.
     * @param dto El DTO de creación con los datos iniciales.
     * @return El DTO de la observación creada.
     */
    @Transactional
    public ObservacionDiariaDTO createObservacion(ObservacionDiariaCreateDTO dto) {
        // 1. Verificar si el Alumno existe
        Alumno alumno = alumnoRepository.findById(dto.getAlumnoId())
            .orElseThrow(() -> new EntityNotFoundException("Alumno no encontrado con ID: " + dto.getAlumnoId()));

        // 2. Mapear DTO a Entidad (directamente en el servicio)
        ObservacionDiaria newEntity = new ObservacionDiaria();
        newEntity.setAlumno(alumno);
        newEntity.setFecha(dto.getFecha());
        newEntity.setActividades(dto.getActividades());
        newEntity.setExplicaciones(dto.getExplicaciones());
        newEntity.setObservacionesAlumno(dto.getObservacionesAlumno());
        newEntity.setHorasRealizadas(dto.getHorasRealizadas());
        newEntity.setFechaCreacion(LocalDateTime.now());
        // observacionesTutor se deja null por defecto

        // 3. Guardar y retornar DTO
        ObservacionDiaria savedEntity = observacionDiariaRepository.save(newEntity);
        return toDTO(savedEntity);
    }
    
    /**
     * Recupera todas las observaciones diarias.
     */
    @Transactional(readOnly = true)
    public List<ObservacionDiariaDTO> findAll() {
        return toDTOList(observacionDiariaRepository.findAll());
    }

    /**
     * Recupera una observación diaria por su ID.
     */
    @Transactional(readOnly = true)
    public Optional<ObservacionDiariaDTO> findById(Long id) {
        return observacionDiariaRepository.findById(id).map(this::toDTO);
    }

    /**
     * Actualiza una Observación Diaria existente con los datos del DTO.
     * @param id El ID de la observación a actualizar.
     * @param dto Los campos a modificar.
     * @return El DTO de la observación actualizada.
     */
    @Transactional
    public ObservacionDiariaDTO updateObservacion(Long id, ObservacionDiariaUpdateDTO dto) {
        ObservacionDiaria entity = observacionDiariaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Observación Diaria no encontrada con ID: " + id));

        // Aplicar los cambios del DTO a la entidad (sin Mapper)
        if (dto.getFecha() != null) {
            entity.setFecha(dto.getFecha());
        }
        if (dto.getActividades() != null) {
            entity.setActividades(dto.getActividades());
        }
        if (dto.getExplicaciones() != null) {
            entity.setExplicaciones(dto.getExplicaciones());
        }
        // Nota: Si el campo de texto se envía como vacío (""), se actualiza, si es null, se ignora.
        if (dto.getObservacionesAlumno() != null) {
            entity.setObservacionesAlumno(dto.getObservacionesAlumno());
        }
        if (dto.getHorasRealizadas() != null) {
            entity.setHorasRealizadas(dto.getHorasRealizadas());
        }

        // Campo exclusivo del tutor (se asume que el controlador/seguridad valida quién llama)
        if (dto.getObservacionesTutor() != null) {
            entity.setObservacionesTutor(dto.getObservacionesTutor());
        }

        ObservacionDiaria updatedEntity = observacionDiariaRepository.save(entity);
        return toDTO(updatedEntity);
    }

    /**
     * Elimina una observación diaria por su ID.
     */
    @Transactional
    public void deleteObservacion(Long id) {
        if (!observacionDiariaRepository.existsById(id)) {
            throw new EntityNotFoundException("Observación Diaria no encontrada con ID: " + id);
        }
        observacionDiariaRepository.deleteById(id);
    }
}
