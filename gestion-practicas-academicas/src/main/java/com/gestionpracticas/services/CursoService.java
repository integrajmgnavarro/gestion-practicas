package com.gestionpracticas.services;

import com.gestionpracticas.dto.CursoCreateDTO;
import com.gestionpracticas.dto.CursoDTO;
import com.gestionpracticas.dto.CursoUpdateDTO;
import com.gestionpracticas.exception.BusinessException;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.models.Curso;
import com.gestionpracticas.models.TutorCurso;
import com.gestionpracticas.repositories.CursoRepository;
import com.gestionpracticas.repositories.TutorCursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    // Repositorio necesario para buscar y asignar la entidad TutorCurso relacionada
    private final TutorCursoRepository tutorCursoRepository; 

    // =================================== MÉTODOS PÚBLICOS (CRUD) ===================================

    @Transactional
    public CursoDTO createCurso(CursoCreateDTO createDTO) {
        // 1. Validar unicidad del código
        if (cursoRepository.existsByCodigo(createDTO.getCodigo())) {
            throw new DuplicateResourceException("Ya existe un curso con el código: " + createDTO.getCodigo());
        }

        // 2. Mapear DTO a entidad (incluyendo la relación TutorCurso)
        Curso curso = toEntity(createDTO);

        // 3. Guardar y retornar el DTO
        Curso savedCurso = cursoRepository.save(curso);
        return toDTO(savedCurso);
    }

    @Transactional(readOnly = true)
    public CursoDTO getCursoById(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));
        return toDTO(curso);
    }

    @Transactional(readOnly = true)
    public List<CursoDTO> getAllCursos() {
        return cursoRepository.findAll().stream()
                .map(CursoService::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CursoDTO updateCurso(CursoUpdateDTO updateDTO) {
        Long id = updateDTO.getId();
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));

        // 1. Validar unicidad del código (excluyendo el curso actual)
        Optional<Curso> existingCurso = cursoRepository.findByCodigo(updateDTO.getCodigo());
        if (existingCurso.isPresent() && !existingCurso.get().getId().equals(id)) {
            throw new DuplicateResourceException("Ya existe otro curso con el código: " + updateDTO.getCodigo());
        }

        // 2. Aplicar cambios del DTO a la entidad
        toUpdateEntity(curso, updateDTO);

        // 3. Guardar y retornar el DTO
        Curso updatedCurso = cursoRepository.save(curso);
        return toDTO(updatedCurso);
    }

    @Transactional
    public void deleteCurso(Long id) {
        // 1. Verificar existencia
        if (!cursoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Curso no encontrado con ID: " + id);
        }

        // 2. Validación de negocio: No se puede eliminar si tiene alumnos asociados
        if (cursoRepository.hasAlumnos(id)) {
            throw new BusinessException("No se puede eliminar el curso porque tiene alumnos asociados. Primero desasocie todos los alumnos.");
        }

        cursoRepository.deleteById(id);
    }


    // =================================== MÉTODOS DE MAPEO ===================================

    /**
     * Mapea una entidad Curso a un DTO de lectura (CursoDTO). (Método estático)
     */
    public static CursoDTO toDTO(Curso curso) {
        CursoDTO dto = new CursoDTO();
        dto.setId(curso.getId());
        dto.setNombre(curso.getNombre());
        dto.setCodigo(curso.getCodigo());
        dto.setDescripcion(curso.getDescripcion());
        dto.setDuracion(curso.getDuracion());
        dto.setFechaInicio(curso.getFechaInicio());
        dto.setFechaFin(curso.getFechaFin());
        dto.setActivo(curso.getActivo());
        dto.setFechaCreacion(curso.getFechaCreacion());

        // Mapeo de la relación TutorCurso (manejo de Lazy loading y posible nulo)
        if (curso.getTutorCurso() != null) {
            TutorCurso tutor = curso.getTutorCurso();
            dto.setTutorCursoId(tutor.getId());
            // Asumimos que TutorCurso tiene nombre y apellidos para construir el nombre completo
            dto.setTutorCursoNombre(tutor.getNombre() + " " + tutor.getApellidos()); 
        } else {
            dto.setTutorCursoId(null);
            dto.setTutorCursoNombre("Sin asignar");
        }

        return dto;
    }

    /**
     * Mapea un DTO de creación a una entidad Curso. (Método de instancia)
     */
    private Curso toEntity(CursoCreateDTO createDTO) {
        Curso curso = new Curso();
        curso.setNombre(createDTO.getNombre());
        curso.setCodigo(createDTO.getCodigo());
        curso.setDescripcion(createDTO.getDescripcion());
        curso.setDuracion(createDTO.getDuracion());
        curso.setFechaInicio(createDTO.getFechaInicio());
        curso.setFechaFin(createDTO.getFechaFin());
        if (createDTO.getActivo() != null) {
            curso.setActivo(createDTO.getActivo());
        }

        // Manejo de la relación TutorCurso
        setTutorCurso(curso, createDTO.getTutorCursoId());
        
        return curso;
    }

    /**
     * Aplica los cambios de un DTO de actualización a una entidad Curso existente. (Método de instancia)
     */
    private void toUpdateEntity(Curso curso, CursoUpdateDTO updateDTO) {
        curso.setNombre(updateDTO.getNombre());
        curso.setCodigo(updateDTO.getCodigo());
        curso.setDescripcion(updateDTO.getDescripcion());
        curso.setDuracion(updateDTO.getDuracion());
        curso.setFechaInicio(updateDTO.getFechaInicio());
        curso.setFechaFin(updateDTO.getFechaFin());
        curso.setActivo(updateDTO.getActivo());

        // Manejo de la relación TutorCurso
        setTutorCurso(curso, updateDTO.getTutorCursoId());
    }
    
    /**
     * Método auxiliar para buscar y asignar el TutorCurso.
     */
    private void setTutorCurso(Curso curso, Long tutorCursoId) {
        if (tutorCursoId != null) {
            TutorCurso tutorCurso = tutorCursoRepository.findById(tutorCursoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tutor de Curso no encontrado con ID: " + tutorCursoId));
            curso.setTutorCurso(tutorCurso);
        } else {
            curso.setTutorCurso(null);
        }
    }
}
