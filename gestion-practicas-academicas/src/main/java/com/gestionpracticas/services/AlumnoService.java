package com.gestionpracticas.services;

import com.gestionpracticas.dto.AlumnoCreateDTO;
import com.gestionpracticas.dto.AlumnoDTO;
import com.gestionpracticas.dto.AlumnoUpdateDTO;
import com.gestionpracticas.exception.BusinessException;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.models.*;
import com.gestionpracticas.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;
    private final EmpresaRepository empresaRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final TutorCursoRepository tutorCursoRepository; 

    // ==========================================================
    // --- UTILITY: Mapeo de Entidad a DTO (para formulario de edición) ---
    // ==========================================================
    
    /**
     * Convierte una entidad Alumno a un DTO de actualización para pre-cargar formularios.
     * Es estático para ser usado directamente por el Controller al obtener datos.
     */
    public static AlumnoUpdateDTO toUpdateDTO(Alumno alumno) {
        AlumnoUpdateDTO dto = new AlumnoUpdateDTO();
        dto.setId(alumno.getId());
        dto.setNombre(alumno.getNombre());
        dto.setApellidos(alumno.getApellidos());
        dto.setDni(alumno.getDni());
        dto.setEmail(alumno.getEmail());
        dto.setTelefono(alumno.getTelefono());
        dto.setFechaNacimiento(alumno.getFechaNacimiento());

        // Datos de Prácticas
        dto.setDuracionPracticas(alumno.getDuracionPracticas());
        dto.setHorario(alumno.getHorario());
        dto.setFechaInicio(alumno.getFechaInicio());
        dto.setFechaFin(alumno.getFechaFin());
        dto.setContratado(alumno.getContratado());

        // Relaciones (Asignar IDs)
        if (alumno.getCurso() != null) {
            dto.setCursoId(alumno.getCurso().getId());
        }
        if (alumno.getEmpresa() != null) {
            dto.setEmpresaId(alumno.getEmpresa().getId());
        }
        if (alumno.getTutorPracticas() != null) {
            dto.setTutorPracticasId(alumno.getTutorPracticas().getId());
        }
        if (alumno.getTutorCurso() != null) { 
            dto.setTutorCursoId(alumno.getTutorCurso().getId());
        }
        return dto;
    }
    
    // ==========================================================
    // --- LÓGICA DE NEGOCIO: CRUD BÁSICO ---
    // ==========================================================
    
    /**
     * Obtiene la lista de todos los alumnos, mapeados a DTO.
     */
    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAllAlumnos() {
        return alumnoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un alumno por su ID, mapeado a DTO (para vista/lectura).
     */
    @Transactional(readOnly = true)
    public AlumnoDTO getAlumnoById(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id: " + id));
        return convertToDTO(alumno);
    }
    
    /**
     * Obtiene la ENTIDAD Alumno por su ID (para ser usada internamente o en el Controller de edición).
     * ESTE MÉTODO RESUELVE EL ERROR EN ADMINALUMNOCONTROLLER.
     */
    @Transactional(readOnly = true)
    public Alumno getAlumnoEntityById(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id: " + id));
    }
    
    /**
     * Crea un nuevo alumno.
     */
    @Transactional
    public AlumnoDTO createAlumno(AlumnoCreateDTO createDTO) {
        // 1. Validar unicidad
        validarDatosUnicos(createDTO.getDni(), createDTO.getEmail(), null);
        
        // 2. Mapear DTO a Entidad
        Alumno alumno = new Alumno();
        alumno.setNombre(createDTO.getNombre());
        alumno.setApellidos(createDTO.getApellidos());
        alumno.setDni(createDTO.getDni());
        alumno.setEmail(createDTO.getEmail());
        alumno.setTelefono(createDTO.getTelefono());
        alumno.setFechaNacimiento(createDTO.getFechaNacimiento());
        alumno.setContratado(false); // Default
        alumno.setActivo(true); // Default

        // 3. Establecer Relaciones (Obligatorio: Curso)
        Curso curso = cursoRepository.findById(createDTO.getCursoId())
            .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + createDTO.getCursoId()));
        alumno.setCurso(curso);
        
        // Relaciones Opcionales
        if (createDTO.getEmpresaId() != null) {
            Empresa empresa = empresaRepository.findById(createDTO.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + createDTO.getEmpresaId()));
            alumno.setEmpresa(empresa);
        }
        if (createDTO.getTutorPracticasId() != null) {
            TutorPracticas tutorPracticas = tutorPracticasRepository.findById(createDTO.getTutorPracticasId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de Prácticas no encontrado con ID: " + createDTO.getTutorPracticasId()));
            alumno.setTutorPracticas(tutorPracticas);
        }
        if (createDTO.getTutorCursoId() != null) {
            TutorCurso tutorCurso = tutorCursoRepository.findById(createDTO.getTutorCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de Curso no encontrado con ID: " + createDTO.getTutorCursoId()));
            alumno.setTutorCurso(tutorCurso);
        }

        // 4. Guardar y devolver DTO
        Alumno savedAlumno = alumnoRepository.save(alumno);
        return convertToDTO(savedAlumno);
    }
    
    /**
     * Actualiza un alumno existente.
     */
    @Transactional
    public AlumnoDTO updateAlumno(AlumnoUpdateDTO dto) {
        Alumno alumno = alumnoRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con ID: " + dto.getId()));

        // Aseguramos que los datos únicos (DNI, Email) no estén duplicados por otro alumno
        validarDatosUnicos(dto.getDni(), dto.getEmail(), dto.getId());

        // Actualización de campos personales
        alumno.setNombre(dto.getNombre());
        alumno.setApellidos(dto.getApellidos());
        alumno.setDni(dto.getDni());
        alumno.setEmail(dto.getEmail());
        alumno.setTelefono(dto.getTelefono());
        alumno.setFechaNacimiento(dto.getFechaNacimiento());

        // Actualización de datos de prácticas
        alumno.setDuracionPracticas(dto.getDuracionPracticas());
        alumno.setHorario(dto.getHorario());
        alumno.setFechaInicio(dto.getFechaInicio());
        alumno.setFechaFin(dto.getFechaFin());
        alumno.setContratado(dto.getContratado());

        // Actualización de Relaciones (manejo de IDs)

        // Curso (Obligatorio)
        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + dto.getCursoId()));
        alumno.setCurso(curso);

        // Empresa (Opcional)
        if (dto.getEmpresaId() != null) {
            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + dto.getEmpresaId()));
            alumno.setEmpresa(empresa);
        } else {
            alumno.setEmpresa(null);
        }

        // Tutor de Prácticas (Opcional)
        if (dto.getTutorPracticasId() != null) {
            TutorPracticas tutorPracticas = tutorPracticasRepository.findById(dto.getTutorPracticasId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tutor de Prácticas no encontrado con ID: " + dto.getTutorPracticasId()));
            alumno.setTutorPracticas(tutorPracticas);
        } else {
            alumno.setTutorPracticas(null);
        }
        
        // Tutor de Curso (Opcional)
        if (dto.getTutorCursoId() != null) {
            TutorCurso tutorCurso = tutorCursoRepository.findById(dto.getTutorCursoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tutor de Curso no encontrado con ID: " + dto.getTutorCursoId()));
            alumno.setTutorCurso(tutorCurso);
        } else {
            alumno.setTutorCurso(null);
        }

        Alumno updatedAlumno = alumnoRepository.save(alumno);
        return convertToDTO(updatedAlumno);
    }
    
    /**
     * Elimina un alumno.
     */
    @Transactional
    public void deleteAlumno(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado para eliminar con ID: " + id));
        
        // Podrías implementar un borrado suave (soft delete) aquí si fuera necesario:
        // alumno.setActivo(false);
        // alumnoRepository.save(alumno);
        
        // Borrado físico
        alumnoRepository.delete(alumno);
    }

    // ==========================================================
    // --- MÉTODOS AUXILIARES ---
    // ==========================================================

    /**
     * Valida que el DNI y el Email no estén ya registrados por otro alumno.
     */
    private void validarDatosUnicos(String dni, String email, Long alumnoId) {
        alumnoRepository.findByDni(dni).ifPresent(a -> {
            if (alumnoId == null || !a.getId().equals(alumnoId)) {
                throw new DuplicateResourceException("Ya existe un alumno con el DNI: " + dni);
            }
        });
        alumnoRepository.findByEmail(email).ifPresent(a -> {
            if (alumnoId == null || !a.getId().equals(alumnoId)) {
                throw new DuplicateResourceException("Ya existe un alumno con el email: " + email);
            }
        });
    }
    
    /**
     * Mapea la entidad Alumno a su DTO de respuesta (AlumnoDTO).
     * Se han quitado los setters de ID para resolver los errores de compilación reportados.
     */
    private AlumnoDTO convertToDTO(Alumno alumno) {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setId(alumno.getId());
        dto.setNombre(alumno.getNombre());
        dto.setApellidos(alumno.getApellidos());
        dto.setDni(alumno.getDni());
        dto.setEmail(alumno.getEmail());
        dto.setTelefono(alumno.getTelefono());
        dto.setFechaNacimiento(alumno.getFechaNacimiento());

        // Datos de Prácticas
        dto.setDuracionPracticas(alumno.getDuracionPracticas());
        dto.setHorario(alumno.getHorario());
        dto.setFechaInicio(alumno.getFechaInicio());
        dto.setFechaFin(alumno.getFechaFin());
        dto.setContratado(alumno.getContratado());
        
        // Relaciones (Solo seteamos el nombre para el DTO de lectura/lista)
        if (alumno.getCurso() != null) {
            // dto.setCursoId(alumno.getCurso().getId()); // Eliminado para resolver error
            dto.setCursoNombre(alumno.getCurso().getNombre());
        }
        if (alumno.getEmpresa() != null) {
            // dto.setEmpresaId(alumno.getEmpresa().getId()); // Eliminado para resolver error
            dto.setEmpresaNombre(alumno.getEmpresa().getNombre());
        }
        if (alumno.getTutorPracticas() != null) {
            // dto.setTutorPracticasId(alumno.getTutorPracticas().getId()); // Eliminado para resolver error
            dto.setTutorPracticasNombre(alumno.getTutorPracticas().getNombreCompleto());
        }
        if (alumno.getTutorCurso() != null) {
            // dto.setTutorCursoId(alumno.getTutorCurso().getId()); // Eliminado para resolver error
            dto.setTutorCursoNombre(alumno.getTutorCurso().getNombreCompleto());
        }

        dto.setActivo(alumno.getActivo());
        dto.setFechaCreacion(alumno.getFechaCreacion());
        return dto;
    }
}
