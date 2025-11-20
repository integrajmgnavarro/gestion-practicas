package com.gestionpracticas.services;

import com.gestionpracticas.dto.TutorCursoCreateDTO;
import com.gestionpracticas.dto.TutorCursoDTO;
import com.gestionpracticas.dto.TutorCursoUpdateDTO;
import com.gestionpracticas.exception.BusinessException;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.models.TutorCurso;
import com.gestionpracticas.repositories.TutorCursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable; 
import org.springframework.data.jpa.domain.Specification; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; 

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList; 

@Service
@RequiredArgsConstructor
public class TutorCursoService {

    private final TutorCursoRepository tutorCursoRepository;

    // ========================= MÉTODOS PÚBLICOS DE CONSULTA ========================= //
    
    /**
     * Devuelve una página de tutores de curso, aplicando filtros dinámicos.
     * 💥 CORREGIDO: Asegura la firma con los 4 String, 1 Boolean y Pageable.
     */
    @Transactional(readOnly = true)
    public Page<TutorCursoDTO> findAll(
            String nombre,
            String apellidos,
            String dni,
            String especialidad, 
            Boolean activo,
            Pageable pageable) {

        Specification<TutorCurso> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(nombre)) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(apellidos)) {
                predicates.add(cb.like(cb.lower(root.get("apellidos")), "%" + apellidos.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(dni)) {
                predicates.add(cb.like(root.get("dni"), "%" + dni + "%"));
            }
            if (StringUtils.hasText(especialidad)) { 
                predicates.add(cb.like(cb.lower(root.get("especialidad")), "%" + especialidad.toLowerCase() + "%"));
            }
            if (activo != null) {
                predicates.add(cb.equal(root.get("activo"), activo));
            }

            // Ordenación por defecto si no se especifica
            if (pageable.getSort().isUnsorted()) {
                query.orderBy(cb.asc(root.get("apellidos")), cb.asc(root.get("nombre")));
            }
            
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return tutorCursoRepository.findAll(spec, pageable).map(this::convertToDTO);
    }
    
    /**
     * Devuelve un listado de todos los tutores de curso activos.
     * Útil para llenar listas desplegables (selects) en formularios.
     * 💥 CORREGIDO: Soluciona el error 'findAllList() is undefined'.
     */
    @Transactional(readOnly = true)
    public List<TutorCursoDTO> findAllList() {
        return tutorCursoRepository.findByActivo(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========================= MÉTODOS PÚBLICOS CRUD ========================= //

    /**
     * Crea un nuevo tutor de curso.
     */
    @Transactional
    public TutorCursoDTO createTutorCurso(TutorCursoCreateDTO dto) {
        checkDuplicateDniAndEmail(null, dto.getDni(), dto.getEmail());

        TutorCurso tutor = new TutorCurso();
        tutor.setNombre(dto.getNombre());
        tutor.setApellidos(dto.getApellidos());
        tutor.setDni(dto.getDni());
        tutor.setEmail(dto.getEmail());
        tutor.setTelefono(dto.getTelefono());
        tutor.setEspecialidad(dto.getEspecialidad());
        // 'activo' por defecto a 'true' según la entidad

        return convertToDTO(tutorCursoRepository.save(tutor));
    }

    /**
     * Actualiza un tutor de curso existente.
     * 💥 NOTA: Este método SÓLO espera el DTO que incluye el ID.
     */
    @Transactional
    public TutorCursoDTO updateTutorCurso(TutorCursoUpdateDTO dto) {
        TutorCurso tutor = tutorCursoRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de Curso no encontrado con ID: " + dto.getId()));

        checkDuplicateDniAndEmail(dto.getId(), dto.getDni(), dto.getEmail());
        
        tutor.setNombre(dto.getNombre());
        tutor.setApellidos(dto.getApellidos());
        tutor.setDni(dto.getDni());
        tutor.setEmail(dto.getEmail());
        tutor.setTelefono(dto.getTelefono());
        tutor.setEspecialidad(dto.getEspecialidad());
        tutor.setActivo(dto.getActivo()); 
        
        return convertToDTO(tutorCursoRepository.save(tutor));
    }

    /**
     * Busca un tutor de curso por su ID.
     */
    @Transactional(readOnly = true)
    public TutorCursoDTO findById(Long id) {
        return tutorCursoRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de Curso no encontrado con ID: " + id));
    }

    /**
     * Elimina un tutor de curso por su ID.
     */
    @Transactional
    public void deleteTutorCurso(Long id) {
        TutorCurso tutor = tutorCursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de Curso no encontrado con ID: " + id));

        // Validación de negocio: No se puede eliminar si tiene alumnos o cursos asignados
        // Se asume la existencia de existsByAlumnosIsNotEmpty y existsByCursosIsNotEmpty en el Repository
        if (tutorCursoRepository.existsByAlumnosIsNotEmpty(id)) {
            throw new BusinessException("No se puede eliminar el tutor porque tiene alumnos asignados.");
        }
        if (tutorCursoRepository.existsByCursosIsNotEmpty(id)) {
            throw new BusinessException("No se puede eliminar el tutor porque tiene cursos asignados.");
        }

        tutorCursoRepository.delete(tutor);
    }
    
    // ========================= MÉTODOS PRIVADOS Y DE UTILIDAD ========================= //

    /**
     * Verifica la unicidad de DNI y Email.
     */
    private void checkDuplicateDniAndEmail(Long tutorId, String dni, String email) {
        tutorCursoRepository.findByDni(dni).ifPresent(t -> {
            if (tutorId == null || !t.getId().equals(tutorId)) {
                throw new DuplicateResourceException("Ya existe un tutor de curso con el DNI: " + dni);
            }
        });
        tutorCursoRepository.findByEmail(email).ifPresent(t -> {
            if (tutorId == null || !t.getId().equals(tutorId)) {
                throw new DuplicateResourceException("Ya existe un tutor de curso con el email: " + email);
            }
        });
    }

    /**
     * Mapea la entidad a su DTO de lectura.
     */
    private TutorCursoDTO convertToDTO(TutorCurso t) {
        TutorCursoDTO dto = new TutorCursoDTO();
        dto.setId(t.getId());
        dto.setNombre(t.getNombre());
        dto.setApellidos(t.getApellidos());
        dto.setDni(t.getDni());
        dto.setEmail(t.getEmail());
        dto.setTelefono(t.getTelefono());
        dto.setEspecialidad(t.getEspecialidad());
        
        dto.setActivo(t.getActivo());
        dto.setFechaCreacion(t.getFechaCreacion());
        
        // Mapeo de cursos asignados para el DTO de lectura
        if (t.getCursos() != null) {
            dto.setNombresCursos(t.getCursos().stream()
                    .map(curso -> curso.getNombre())
                    .collect(Collectors.toList()));
            // Contar los cursos/módulos asignados
            dto.setModulosAsignados(t.getCursos().size()); 
        } else {
             dto.setModulosAsignados(0);
        }

        return dto;
    }
}