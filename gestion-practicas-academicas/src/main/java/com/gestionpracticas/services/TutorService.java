package com.gestionpracticas.services;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.models.*;
import com.gestionpracticas.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final TutorCursoRepository tutorCursoRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final EmpresaRepository empresaRepository;

    // ========================= TUTORES DE CURSO ========================= //

    @Transactional
    public TutorCursoDTO createTutorCurso(TutorCursoCreateDTO dto) {
        validarDatosUnicosTutorCurso(dto.getDni(), dto.getEmail(), null);

        TutorCurso tutor = new TutorCurso();
        tutor.setNombre(dto.getNombre());
        tutor.setApellidos(dto.getApellidos());
        tutor.setDni(dto.getDni());
        tutor.setEmail(dto.getEmail());
        tutor.setTelefono(dto.getTelefono());
        tutor.setEspecialidad(dto.getEspecialidad());
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

        if (dto.getNombre() != null) tutor.setNombre(dto.getNombre());
        if (dto.getApellidos() != null) tutor.setApellidos(dto.getApellidos());
        if (dto.getEmail() != null) tutor.setEmail(dto.getEmail());
        if (dto.getTelefono() != null) tutor.setTelefono(dto.getTelefono());
        if (dto.getEspecialidad() != null) tutor.setEspecialidad(dto.getEspecialidad());
        if (dto.getActivo() != null) tutor.setActivo(dto.getActivo());

        tutor = tutorCursoRepository.save(tutor);
        return convertToDTO(tutor);
    }

    @Transactional
    public void deleteTutorCurso(Long id) {
        TutorCurso tutor = tutorCursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de curso no encontrado"));
        tutorCursoRepository.delete(tutor);
    }

    // ========================= TUTORES DE PRÁCTICAS ========================= //

    @Transactional
    public TutorPracticasDTO createTutorPracticas(TutorPracticasCreateDTO dto) {
        validarDatosUnicosTutorPracticas(dto.getDni(), dto.getEmail(), null);

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id: " + dto.getEmpresaId()));

        TutorPracticas tutor = new TutorPracticas();
        tutor.setNombre(dto.getNombre());
        tutor.setApellidos(dto.getApellidos());
        tutor.setDni(dto.getDni());
        tutor.setEmail(dto.getEmail());
        tutor.setTelefono(dto.getTelefono());
        tutor.setCargo(dto.getCargo());
        tutor.setHorario(dto.getHorario());
        tutor.setEmpresa(empresa);
        tutor.setActivo(true);

        tutor = tutorPracticasRepository.save(tutor);
        return convertToDTO(tutor);
    }

    @Transactional(readOnly = true)
    public TutorPracticasDTO getTutorPracticasById(Long id) {
        TutorPracticas tutor = tutorPracticasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de prácticas no encontrado con id: " + id));
        return convertToDTO(tutor);
    }

    @Transactional(readOnly = true)
    public List<TutorPracticasDTO> getAllTutoresPracticas() {
        return tutorPracticasRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TutorPracticasDTO updateTutorPracticas(Long id, TutorPracticasUpdateDTO dto) {
        TutorPracticas tutor = tutorPracticasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de prácticas no encontrado"));

        if (dto.getNombre() != null) tutor.setNombre(dto.getNombre());
        if (dto.getApellidos() != null) tutor.setApellidos(dto.getApellidos());
        if (dto.getEmail() != null) tutor.setEmail(dto.getEmail());
        if (dto.getTelefono() != null) tutor.setTelefono(dto.getTelefono());
        if (dto.getCargo() != null) tutor.setCargo(dto.getCargo());
        if (dto.getHorario() != null) tutor.setHorario(dto.getHorario());
        if (dto.getActivo() != null) tutor.setActivo(dto.getActivo());

        tutor = tutorPracticasRepository.save(tutor);
        return convertToDTO(tutor);
    }

    @Transactional
    public void deleteTutorPracticas(Long id) {
        TutorPracticas tutor = tutorPracticasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de prácticas no encontrado"));
        tutorPracticasRepository.delete(tutor);
    }

    // ========================= MÉTODOS PRIVADOS ========================= //

    private void validarDatosUnicosTutorCurso(String dni, String email, Long tutorId) {
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

    private void validarDatosUnicosTutorPracticas(String dni, String email, Long tutorId) {
        tutorPracticasRepository.findByDni(dni).ifPresent(t -> {
            if (tutorId == null || !t.getId().equals(tutorId)) {
                throw new DuplicateResourceException("Ya existe un tutor de prácticas con el DNI: " + dni);
            }
        });
        tutorPracticasRepository.findByEmail(email).ifPresent(t -> {
            if (tutorId == null || !t.getId().equals(tutorId)) {
                throw new DuplicateResourceException("Ya existe un tutor de prácticas con el email: " + email);
            }
        });
    }

    // Conversores genéricos

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
        dto.setFechaActualizacion(t.getFechaActualizacion());
        return dto;
    }

    private TutorPracticasDTO convertToDTO(TutorPracticas t) {
        TutorPracticasDTO dto = new TutorPracticasDTO();
        dto.setId(t.getId());
        dto.setNombre(t.getNombre());
        dto.setApellidos(t.getApellidos());
        dto.setDni(t.getDni());
        dto.setEmail(t.getEmail());
        dto.setTelefono(t.getTelefono());
        dto.setCargo(t.getCargo());
        dto.setHorario(t.getHorario());
        if (t.getEmpresa() != null) {
            dto.setEmpresaId(t.getEmpresa().getId());
            dto.setEmpresaNombre(t.getEmpresa().getNombre());
        }
        dto.setActivo(t.getActivo());
        dto.setFechaCreacion(t.getFechaCreacion());
        dto.setFechaActualizacion(t.getFechaActualizacion());
        return dto;
    }
}

