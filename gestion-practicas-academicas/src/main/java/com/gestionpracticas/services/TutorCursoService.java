package com.gestionpracticas.services;

import com.gestionpracticas.dto.TutorCursoCreateDTO;
import com.gestionpracticas.dto.TutorCursoDTO;
import com.gestionpracticas.dto.TutorCursoUpdateDTO;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.models.TutorCurso;
import com.gestionpracticas.repositories.TutorCursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TutorCursoService {

    private final TutorCursoRepository tutorCursoRepository;

    @Transactional
    public TutorCursoDTO createTutorCurso(TutorCursoCreateDTO dto) {
        validarDatosUnicos(dto.getDni(), dto.getEmail(), null);

        TutorCurso tutor = new TutorCurso();
        tutor.setNombre(dto.getNombre());
        tutor.setApellidos(dto.getApellidos());
        tutor.setDni(dto.getDni());
        tutor.setEmail(dto.getEmail());
        tutor.setTelefono(dto.getTelefono());
        tutor.setDepartamento(dto.getDepartamento()); 
        tutor.setActivo(true);

        tutor = tutorCursoRepository.save(tutor);
        return convertToDTO(tutor);
    }

    @Transactional(readOnly = true)
    public TutorCursoDTO getTutorCursoById(Long id) {
        TutorCurso tutor = tutorCursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de curso no encontrado con id: " + id));
        return convertToDTO(tutor);
    }

    @Transactional(readOnly = true)
    public List<TutorCursoDTO> getAllTutoresCurso() {
        return tutorCursoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TutorCursoDTO updateTutorCurso(Long id, TutorCursoUpdateDTO dto) {
        TutorCurso tutor = tutorCursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de curso no encontrado"));

        validarDatosUnicos(dto.getDni(), dto.getEmail(), id);
        
        if (dto.getNombre() != null) tutor.setNombre(dto.getNombre());
        if (dto.getApellidos() != null) tutor.setApellidos(dto.getApellidos());
        if (dto.getEmail() != null) tutor.setEmail(dto.getEmail());
        if (dto.getTelefono() != null) tutor.setTelefono(dto.getTelefono());
        if (dto.getDepartamento() != null) tutor.setDepartamento(dto.getDepartamento());
        if (dto.getActivo() != null) tutor.setActivo(dto.getActivo());

        tutor = tutorCursoRepository.save(tutor);
        return convertToDTO(tutor);
    }

    @Transactional
    public void deleteTutorCurso(Long id) {
        TutorCurso tutor = tutorCursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de curso no encontrado"));
        // Aquí se debería añadir la lógica de BusinessException para evitar borrar si tiene alumnos/cursos asociados
        tutorCursoRepository.delete(tutor);
    }

    private void validarDatosUnicos(String dni, String email, Long tutorId) {
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

    private TutorCursoDTO convertToDTO(TutorCurso t) {
        TutorCursoDTO dto = new TutorCursoDTO();
        dto.setId(t.getId());
        dto.setNombre(t.getNombre());
        dto.setApellidos(t.getApellidos());
        dto.setDni(t.getDni());
        dto.setEmail(t.getEmail());
        dto.setTelefono(t.getTelefono());
        dto.setDepartamento(t.getDepartamento()); 
        dto.setActivo(t.getActivo());
        dto.setFechaCreacion(t.getFechaCreacion());
        return dto;
    }
}
