package com.gestionpracticas.services;

import com.gestionpracticas.dto.TutorPracticasDTO;
import com.gestionpracticas.dto.TutorPracticasCreateDTO;
import com.gestionpracticas.dto.TutorPracticasUpdateDTO;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.models.Empresa;
import com.gestionpracticas.models.TutorPracticas;
import com.gestionpracticas.repositories.EmpresaRepository;
import com.gestionpracticas.repositories.TutorPracticasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TutorPracticasService {

    private final TutorPracticasRepository tutorPracticasRepository;
    private final EmpresaRepository empresaRepository;

    // ==========================================================
    // --- UTILITY: Mapeo de Entidad a DTO (para formulario de edición) ---
    // ==========================================================
    
    /**
     * Convierte la entidad TutorPracticas a su DTO de actualización.
     */
    public TutorPracticasUpdateDTO convertToUpdateDTO(TutorPracticas tutor) {
        TutorPracticasUpdateDTO updateDTO = new TutorPracticasUpdateDTO();
        updateDTO.setId(tutor.getId());
        updateDTO.setNombre(tutor.getNombre());
        updateDTO.setApellidos(tutor.getApellidos());
        updateDTO.setDni(tutor.getDni());
        updateDTO.setEmail(tutor.getEmail());
        updateDTO.setTelefono(tutor.getTelefono());
        updateDTO.setCargo(tutor.getCargo());
        updateDTO.setHorario(tutor.getHorario());
        
        // CORRECCIÓN (Línea 50): Cambiado isActivo() a getActivo()
        // Resuelve: The method isActivo() is undefined for the type TutorPracticas
        updateDTO.setActivo(tutor.getActivo()); 
        
        if (tutor.getEmpresa() != null) {
            updateDTO.setEmpresaId(tutor.getEmpresa().getId());
        }
        return updateDTO;
    }
    
    // ==========================================================
    // --- LÓGICA DE BÚSQUEDA Y LISTADO ---
    // ==========================================================

    /**
     * Busca tutores de prácticas aplicando filtros y paginación.
     */
    @Transactional(readOnly = true)
    public Page<TutorPracticasDTO> findTutoresByFilters(
            String nombre, String apellidos, String dni, Boolean activo, Pageable pageable) {

        // 1. Crear la especificación dinámica
        Specification<TutorPracticas> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por Nombre (case-insensitive, like)
            if (nombre != null && !nombre.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }
            // Filtro por Apellidos (case-insensitive, like)
            if (apellidos != null && !apellidos.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("apellidos")), "%" + apellidos.toLowerCase() + "%"));
            }
            // Filtro por DNI (case-insensitive, like)
            if (dni != null && !dni.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("dni")), "%" + dni.toLowerCase() + "%"));
            }
            // Filtro por Estado Activo
            if (activo != null) {
                predicates.add(criteriaBuilder.equal(root.get("activo"), activo));
            }

            // Combinar todos los predicados con AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 2. Ejecutar la consulta con la especificación y la paginación
        Page<TutorPracticas> tutorPage = tutorPracticasRepository.findAll(spec, pageable);

        // 3. Mapear la página de entidades a una página de DTOs
        return tutorPage.map(this::convertToDTO);
    }
    
    /**
     * Obtiene todos los tutores de prácticas activos (útil para dropdowns, etc.).
     */
    @Transactional(readOnly = true)
    public List<TutorPracticasDTO> getAllTutoresPracticas() {
        // CORRECCIÓN (Línea 112): Usamos findByActivo(Boolean.TRUE) en lugar de findAllByActivoTrue()
        // Resuelve: The method findAllByActivoTrue() is undefined
        return tutorPracticasRepository.findByActivo(Boolean.TRUE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un Tutor de Prácticas por su ID.
     */
    @Transactional(readOnly = true)
    public TutorPracticasDTO getTutorPracticasById(Long id) {
        TutorPracticas tutor = tutorPracticasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de Prácticas no encontrado con ID: " + id));
        return convertToDTO(tutor);
    }

    // ==========================================================
    // --- LÓGICA DE CREACIÓN ---
    // ==========================================================

    /**
     * Crea un nuevo Tutor de Prácticas.
     */
    @Transactional
    public TutorPracticasDTO createTutorPracticas(TutorPracticasCreateDTO createDTO) {
        checkDuplicateDniAndEmail(null, createDTO.getDni(), createDTO.getEmail());

        Empresa empresa = empresaRepository.findById(createDTO.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + createDTO.getEmpresaId()));

        TutorPracticas tutor = new TutorPracticas();
        // Asignación de campos
        tutor.setNombre(createDTO.getNombre());
        tutor.setApellidos(createDTO.getApellidos());
        tutor.setDni(createDTO.getDni());
        tutor.setEmail(createDTO.getEmail());
        tutor.setTelefono(createDTO.getTelefono());
        tutor.setCargo(createDTO.getCargo());
        tutor.setHorario(createDTO.getHorario());
        tutor.setActivo(Boolean.TRUE); // Nuevo tutor siempre activo
        tutor.setEmpresa(empresa);

        TutorPracticas savedTutor = tutorPracticasRepository.save(tutor);
        return convertToDTO(savedTutor);
    }

    // ==========================================================
    // --- LÓGICA DE ACTUALIZACIÓN ---
    // ==========================================================
    
    /**
     * Actualiza un Tutor de Prácticas existente.
     */
    @Transactional
    public TutorPracticasDTO updateTutorPracticas(Long id, TutorPracticasUpdateDTO updateDTO) {
        TutorPracticas existingTutor = tutorPracticasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de Prácticas no encontrado con ID: " + id));

        checkDuplicateDniAndEmail(id, updateDTO.getDni(), updateDTO.getEmail());
        
        Empresa empresa = empresaRepository.findById(updateDTO.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + updateDTO.getEmpresaId()));

        // Actualización de campos
        existingTutor.setNombre(updateDTO.getNombre());
        existingTutor.setApellidos(updateDTO.getApellidos());
        existingTutor.setDni(updateDTO.getDni());
        existingTutor.setEmail(updateDTO.getEmail());
        existingTutor.setTelefono(updateDTO.getTelefono());
        existingTutor.setCargo(updateDTO.getCargo());
        existingTutor.setHorario(updateDTO.getHorario());
        
        // CORRECCIÓN (Línea 182): Cambiado isActivo() a getActivo() del DTO
        // Resuelve: The method isActivo() is undefined for the type TutorPracticasUpdateDTO
        existingTutor.setActivo(updateDTO.getActivo()); 
        
        existingTutor.setEmpresa(empresa);

        TutorPracticas updatedTutor = tutorPracticasRepository.save(existingTutor);
        return convertToDTO(updatedTutor);
    }

    // ==========================================================
    // --- LÓGICA DE ELIMINACIÓN ---
    // ==========================================================
    
    /**
     * Elimina un Tutor de Prácticas.
     */
    @Transactional
    public void deleteTutorPracticas(Long id) {
        TutorPracticas tutor = tutorPracticasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de Prácticas no encontrado con ID: " + id));
        
        // Aquí se podría añadir lógica de negocio para evitar eliminación si tiene prácticas asociadas
        // if (tutor.getPracticas().size() > 0) {
        //     throw new BusinessException("No se puede eliminar el tutor, tiene prácticas asociadas.");
        // }
        
        tutorPracticasRepository.delete(tutor);
    }

    // ==========================================================
    // --- MÉTODOS PRIVADOS DE UTILIDAD ---
    // ==========================================================
    
    /**
     * Mapea la entidad TutorPracticas a su DTO de respuesta.
     */
    private TutorPracticasDTO convertToDTO(TutorPracticas tutor) {
        TutorPracticasDTO dto = new TutorPracticasDTO();
        dto.setId(tutor.getId());
        dto.setNombre(tutor.getNombre());
        dto.setApellidos(tutor.getApellidos());
        dto.setDni(tutor.getDni());
        dto.setEmail(tutor.getEmail());
        dto.setTelefono(tutor.getTelefono());
        
        // --- ASIGNACIÓN DE CARGO Y HORARIO AL DTO ---
        dto.setCargo(tutor.getCargo());
        dto.setHorario(tutor.getHorario());
        
        // Asignamos el nombre de la empresa
        if (tutor.getEmpresa() != null) {
            dto.setEmpresaNombre(tutor.getEmpresa().getNombre());
        }
        
        // CORRECCIÓN (Línea 234): Cambiado isActivo() a getActivo()
        // Resuelve: The method isActivo() is undefined for the type TutorPracticas
        dto.setActivo(tutor.getActivo()); 
        
        return dto;
    }

    /**
     * Verifica la unicidad de DNI y Email.
     */
    private void checkDuplicateDniAndEmail(Long tutorId, String dni, String email) {
        tutorPracticasRepository.findByDni(dni).ifPresent(t -> {
            if (tutorId == null || !t.getId().equals(tutorId)) {
                throw new DuplicateResourceException("Ya existe un Tutor de Prácticas con el DNI: " + dni);
            }
        });
        tutorPracticasRepository.findByEmail(email).ifPresent(t -> {
            if (tutorId == null || !t.getId().equals(tutorId)) {
                throw new DuplicateResourceException("Ya existe un Tutor de Prácticas con el email: " + email);
            }
        });
    }
}