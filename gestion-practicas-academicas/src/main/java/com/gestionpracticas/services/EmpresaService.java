package com.gestionpracticas.services;

import com.gestionpracticas.dto.EmpresaCreateDTO;
import com.gestionpracticas.dto.EmpresaDTO;
import com.gestionpracticas.dto.EmpresaUpdateDTO;
import com.gestionpracticas.exception.BusinessException;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.models.Empresa;
import com.gestionpracticas.repositories.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    /**
     * Crea una nueva empresa.
     */
    @Transactional
    public EmpresaDTO createEmpresa(EmpresaCreateDTO dto) {
        validarDatosUnicos(dto.getCif(), null);

        Empresa empresa = new Empresa();
        empresa.setNombre(dto.getNombre());
        empresa.setCif(dto.getCif());
        empresa.setDireccion(dto.getDireccion());
        empresa.setTelefono(dto.getTelefono());
        empresa.setEmail(dto.getEmail());
        empresa.setPersonaContacto(dto.getPersonaContacto());
        empresa.setSector(dto.getSector());
        empresa.setActivo(true); // Siempre activa al crear

        empresa = empresaRepository.save(empresa);
        return convertToDTO(empresa);
    }

    /**
     * Obtiene una empresa por su ID.
     */
    @Transactional(readOnly = true)
    public EmpresaDTO getEmpresaById(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));
        return convertToDTO(empresa);
    }

    /**
     * Obtiene la lista de todas las empresas.
     */
    @Transactional(readOnly = true)
    public List<EmpresaDTO> getAllEmpresas() {
        return empresaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza los datos de una empresa existente.
     */
    @Transactional
    public EmpresaDTO updateEmpresa(Long id, EmpresaUpdateDTO dto) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));

        // Validar unicidad del CIF si se está modificando
        if (!empresa.getCif().equals(dto.getCif())) {
            validarDatosUnicos(dto.getCif(), id);
        }

        // Actualizar campos
        empresa.setNombre(dto.getNombre());
        empresa.setCif(dto.getCif());
        empresa.setDireccion(dto.getDireccion());
        empresa.setTelefono(dto.getTelefono());
        empresa.setEmail(dto.getEmail());
        empresa.setPersonaContacto(dto.getPersonaContacto());
        empresa.setSector(dto.getSector());
        empresa.setActivo(dto.getActivo());

        empresa = empresaRepository.save(empresa);
        return convertToDTO(empresa);
    }

    /**
     * Elimina una empresa por su ID.
     */
    @Transactional
    public void deleteEmpresa(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));

        // 1. Verificar si tiene alumnos asociados
        if (empresaRepository.existsByAlumnosIsNotEmpty(id)) {
            throw new BusinessException("No se puede eliminar la empresa porque tiene alumnos asignados.");
        }

        // 2. Verificar si tiene tutores de prácticas asociados
        if (empresaRepository.existsByTutoresPracticasIsNotEmpty(id)) {
            throw new BusinessException("No se puede eliminar la empresa porque tiene tutores de prácticas asociados.");
        }

        empresaRepository.delete(empresa);
    }

    // ========================= MÉTODOS PRIVADOS Y DE UTILIDAD ========================= //

    /**
     * Valida que el CIF sea único en el sistema.
     * @param cif CIF a validar.
     * @param empresaId ID de la empresa a excluir (para operaciones de actualización).
     */
    private void validarDatosUnicos(String cif, Long empresaId) {
        empresaRepository.findByCif(cif).ifPresent(e -> {
            if (empresaId == null || !e.getId().equals(empresaId)) {
                throw new DuplicateResourceException("Ya existe una empresa con el CIF: " + cif);
            }
        });
    }

    /**
     * Convierte la entidad Empresa a EmpresaDTO.
     */
    private EmpresaDTO convertToDTO(Empresa e) {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setCif(e.getCif());
        dto.setDireccion(e.getDireccion());
        dto.setTelefono(e.getTelefono());
        dto.setEmail(e.getEmail());
        dto.setPersonaContacto(e.getPersonaContacto());
        dto.setSector(e.getSector());
        dto.setActivo(e.getActivo());
        dto.setFechaCreacion(e.getFechaCreacion());
        return dto;
    }
}
