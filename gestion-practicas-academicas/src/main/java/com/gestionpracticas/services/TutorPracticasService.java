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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Convierte la entidad TutorPracticas a un DTO de actualización para precargar formularios.
     * Se asume que TutorPracticasUpdateDTO tiene los campos de ID necesarios.
     */
    public static TutorPracticasUpdateDTO toUpdateDTO(TutorPracticas tutor) {
        TutorPracticasUpdateDTO dto = new TutorPracticasUpdateDTO();
        dto.setId(tutor.getId());
        dto.setNombre(tutor.getNombre());
        dto.setApellidos(tutor.getApellidos());
        dto.setDni(tutor.getDni());
        dto.setEmail(tutor.getEmail());
        dto.setTelefono(tutor.getTelefono());
        
        // --- ATRIBUTOS ADICIONALES DE LA ENTIDAD ---
        dto.setCargo(tutor.getCargo());
        dto.setHorario(tutor.getHorario());

        // Relación (Asignar ID de Empresa)
        if (tutor.getEmpresa() != null) {
            dto.setEmpresaId(tutor.getEmpresa().getId());
        }
        return dto;
    }

    // ==========================================================
    // --- LÓGICA DE NEGOCIO: CRUD BÁSICO ---
    // ==========================================================

    @Transactional(readOnly = true)
    public List<TutorPracticasDTO> getAllTutoresPracticas() {
        return tutorPracticasRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Método para obtener la Entidad (necesario para el Controller de Edición)
    @Transactional(readOnly = true)
    public TutorPracticas getTutorPracticasEntityById(Long id) {
        return tutorPracticasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de Prácticas no encontrado con id: " + id));
    }
    
    @Transactional(readOnly = true)
    public TutorPracticasDTO getTutorPracticasById(Long id) {
        TutorPracticas tutor = getTutorPracticasEntityById(id);
        return convertToDTO(tutor);
    }
    
    @Transactional
    public TutorPracticasDTO createTutorPracticas(TutorPracticasCreateDTO createDTO) {
        validarDatosUnicos(createDTO.getDni(), createDTO.getEmail(), null);
        
        TutorPracticas tutor = new TutorPracticas();
        tutor.setNombre(createDTO.getNombre());
        tutor.setApellidos(createDTO.getApellidos());
        tutor.setDni(createDTO.getDni());
        tutor.setEmail(createDTO.getEmail());
        tutor.setTelefono(createDTO.getTelefono());
        
        // --- ASIGNACIÓN DE CARGO Y HORARIO ---
        tutor.setCargo(createDTO.getCargo());
        tutor.setHorario(createDTO.getHorario());
        
        tutor.setActivo(true);

        // Relación con Empresa (Obligatoria o al menos debe existir)
        Empresa empresa = empresaRepository.findById(createDTO.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + createDTO.getEmpresaId()));
        tutor.setEmpresa(empresa);

        TutorPracticas savedTutor = tutorPracticasRepository.save(tutor);
        return convertToDTO(savedTutor);
    }
    
    @Transactional
    public TutorPracticasDTO updateTutorPracticas(TutorPracticasUpdateDTO updateDTO) {
        TutorPracticas tutor = getTutorPracticasEntityById(updateDTO.getId());
        
        validarDatosUnicos(updateDTO.getDni(), updateDTO.getEmail(), updateDTO.getId());
        
        tutor.setNombre(updateDTO.getNombre());
        tutor.setApellidos(updateDTO.getApellidos());
        tutor.setDni(updateDTO.getDni());
        tutor.setEmail(updateDTO.getEmail());
        tutor.setTelefono(updateDTO.getTelefono());
        
        // --- ACTUALIZACIÓN DE CARGO Y HORARIO ---
        tutor.setCargo(updateDTO.getCargo());
        tutor.setHorario(updateDTO.getHorario());
        
        // Actualizar Relación con Empresa
        Empresa empresa = empresaRepository.findById(updateDTO.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + updateDTO.getEmpresaId()));
        tutor.setEmpresa(empresa);
        
        TutorPracticas updatedTutor = tutorPracticasRepository.save(tutor);
        return convertToDTO(updatedTutor);
    }
    
    @Transactional
    public void deleteTutorPracticas(Long id) {
        if (!tutorPracticasRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tutor de Prácticas no encontrado para eliminar con ID: " + id);
        }
        tutorPracticasRepository.deleteById(id);
    }
    
    // ==========================================================
    // --- MÉTODOS AUXILIARES ---
    // ==========================================================
    
    private void validarDatosUnicos(String dni, String email, Long tutorId) {
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
            dto.setEmpresaId(tutor.getEmpresa().getId());
            dto.setEmpresaNombre(tutor.getEmpresa().getNombre());
        }
        
        dto.setActivo(tutor.getActivo());
        dto.setFechaCreacion(tutor.getFechaCreacion());
        return dto;
    }
}
