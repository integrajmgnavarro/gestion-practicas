package com.gestionpracticas.services;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.BusinessException;
import com.gestionpracticas.models.*;
import com.gestionpracticas.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlumnoService { // Implementar la interfaz

    private final AlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final EmpresaRepository empresaRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Repositorios añadidos para los nuevos métodos estadísticos
    private final EvaluacionRepository evaluacionRepository;
    private final ObservacionDiariaRepository observacionDiariaRepository;

    @Transactional
    public AlumnoDTO createAlumno(AlumnoCreateDTO createDTO) {
        // Validaciones
        validarDatosUnicos(createDTO.getDni(), createDTO.getEmail(), null);
        
        // Validar curso existe
        Curso curso = cursoRepository.findById(createDTO.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con id: " + createDTO.getCursoId()));
        
        // Validar empresa si se proporciona
        Empresa empresa = null;
        if (createDTO.getEmpresaId() != null) {
            empresa = empresaRepository.findById(createDTO.getEmpresaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id: " + createDTO.getEmpresaId()));
        }
        
        // Validar tutor de prácticas si se proporciona
        TutorPracticas tutorPracticas = null;
        if (createDTO.getTutorPracticasId() != null) {
            tutorPracticas = tutorPracticasRepository.findById(createDTO.getTutorPracticasId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tutor de prácticas no encontrado con id: " + createDTO.getTutorPracticasId()));
            
            // Validar que el tutor pertenezca a la empresa asignada
            if (empresa != null && !tutorPracticas.getEmpresa().getId().equals(empresa.getId())) {
                throw new BusinessException("El tutor de prácticas no pertenece a la empresa asignada");
            }
        }
        
        // Validar fechas
        if (createDTO.getFechaInicio() != null && createDTO.getFechaFin() != null) {
            if (createDTO.getFechaFin().isBefore(createDTO.getFechaInicio())) {
                throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio");
            }
        }
        
        // Crear User
        Usuario user = new Usuario();
        user.setEmail(createDTO.getEmail());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setActivo(true);
        
        // Asignar rol ALUMNO
        user.setRol(Usuario.Rol.ALUMNO);
        
        user = usuarioRepository.save(user);
        
        // Crear Alumno
        Alumno alumno = new Alumno();
        alumno.setUsuario(user);
        alumno.setNombre(createDTO.getNombre());
        alumno.setApellidos(createDTO.getApellidos());
        alumno.setDni(createDTO.getDni());
        alumno.setFechaNacimiento(createDTO.getFechaNacimiento());
        alumno.setEmail(createDTO.getEmail());
        alumno.setTelefono(createDTO.getTelefono());
        alumno.setCurso(curso);
        alumno.setEmpresa(empresa);
        alumno.setTutorPracticas(tutorPracticas);
        alumno.setDuracionPracticas(createDTO.getDuracionPracticas());
        alumno.setHorario(createDTO.getHorario());
        alumno.setFechaInicio(createDTO.getFechaInicio());
        alumno.setFechaFin(createDTO.getFechaFin());
        alumno.setActivo(true);
        alumno.setContratado(createDTO.getContratado() != null ? createDTO.getContratado() : false);
        
        alumno = alumnoRepository.save(alumno);
        
        // Actualizar referenceId en User
        user.setReferenceId(alumno.getId());
        usuarioRepository.save(user);
        
        return convertToDTO(alumno);
    }
    
    @Transactional(readOnly = true)
    public AlumnoDTO getAlumnoById(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id: " + id));
        return convertToDTO(alumno);
    }
    
    @Transactional(readOnly = true)
    public AlumnoDTO getAlumnoByDni(String dni) {
        Alumno alumno = alumnoRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con DNI: " + dni));
        return convertToDTO(alumno);
    }
    
    @Transactional(readOnly = true)
    public AlumnoDTO getAlumnoByUsuarioId(Long usuarioId) {
        Alumno alumno = alumnoRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado para el usuario: " + usuarioId));
        return convertToDTO(alumno);
    }
    
    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAllAlumnos() {
        return alumnoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAlumnosByCurso(Long cursoId) {
        return alumnoRepository.findByCurso_Id(cursoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAlumnosByEmpresa(Long empresaId) {
        return alumnoRepository.findByEmpresa_Id(empresaId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAlumnosByTutorPracticas(Long tutorPracticasId) {
        return alumnoRepository.findByTutorPracticas_Id(tutorPracticasId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAlumnosByTutorCurso(Long tutorCursoId) {
        return alumnoRepository.findByTutorCurso_Id(tutorCursoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAlumnosActivos() {
        return alumnoRepository.findByActivo(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAlumnosConPracticasActivas() {
        return alumnoRepository.findAlumnosConPracticasActivas().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AlumnoDTO> searchAlumnos(AlumnoSearchDTO searchDTO) {
        return alumnoRepository.findByMultipleCriteria(
                searchDTO.getNombre(),
                searchDTO.getApellidos(),
                searchDTO.getDni(),
                searchDTO.getCursoId(),
                searchDTO.getEmpresaId(),
                searchDTO.getActivo()
        ).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public AlumnoDTO updateAlumno(Long id, AlumnoUpdateDTO updateDTO) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id: " + id));
        
        // Actualizar datos básicos
        if (updateDTO.getNombre() != null) {
            alumno.setNombre(updateDTO.getNombre());
        }
        if (updateDTO.getApellidos() != null) {
            alumno.setApellidos(updateDTO.getApellidos());
        }
        if (updateDTO.getTelefono() != null) {
            alumno.setTelefono(updateDTO.getTelefono());
        }
        
        // Actualizar relaciones
        if (updateDTO.getCursoId() != null) {
            Curso curso = cursoRepository.findById(updateDTO.getCursoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
            alumno.setCurso(curso);
        }
        
        if (updateDTO.getEmpresaId() != null) {
            Empresa empresa = empresaRepository.findById(updateDTO.getEmpresaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));
            alumno.setEmpresa(empresa);
        }
        
        if (updateDTO.getTutorPracticasId() != null) {
            TutorPracticas tutorPracticas = tutorPracticasRepository.findById(updateDTO.getTutorPracticasId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tutor de prácticas no encontrado"));
            
            // Validar que el tutor pertenezca a la empresa
            if (alumno.getEmpresa() != null && 
                !tutorPracticas.getEmpresa().getId().equals(alumno.getEmpresa().getId())) {
                throw new BusinessException("El tutor de prácticas no pertenece a la empresa del alumno");
            }
            alumno.setTutorPracticas(tutorPracticas);
        }
        
        // Actualizar datos de prácticas
        if (updateDTO.getDuracionPracticas() != null) {
            alumno.setDuracionPracticas(updateDTO.getDuracionPracticas());
        }
        if (updateDTO.getHorario() != null) {
            alumno.setHorario(updateDTO.getHorario());
        }
        if (updateDTO.getFechaInicio() != null) {
            alumno.setFechaInicio(updateDTO.getFechaInicio());
        }
        if (updateDTO.getFechaFin() != null) {
            // Validar que fecha fin sea posterior a fecha inicio
            if (alumno.getFechaInicio() != null && 
                updateDTO.getFechaFin().isBefore(alumno.getFechaInicio())) {
                throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio");
            }
            alumno.setFechaFin(updateDTO.getFechaFin());
        }
        if (updateDTO.getContratado() != null) {
            alumno.setContratado(updateDTO.getContratado());
        }
        if (updateDTO.getActivo() != null) {
            alumno.setActivo(updateDTO.getActivo());
            // Actualizar también el estado del usuario
            alumno.getUsuario().setActivo(updateDTO.getActivo());
            usuarioRepository.save(alumno.getUsuario());
        }
        
        alumno = alumnoRepository.save(alumno);
        return convertToDTO(alumno);
    }
    
    @Transactional
    public void deleteAlumno(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id: " + id));
        
        Usuario user = alumno.getUsuario();
        
        // Eliminar alumno (en cascada eliminará observaciones, incidencias, evaluaciones)
        alumnoRepository.delete(alumno);
        
        // Eliminar usuario asociado
        usuarioRepository.delete(user);
    }
    
    @Transactional
    public AlumnoDTO asignarEmpresaYTutor(Long alumnoId, Long empresaId, Long tutorPracticasId) {
        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));
        
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));
        
        TutorPracticas tutorPracticas = tutorPracticasRepository.findById(tutorPracticasId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de prácticas no encontrado"));
        
        // Validar que el tutor pertenezca a la empresa
        if (!tutorPracticas.getEmpresa().getId().equals(empresaId)) {
            throw new BusinessException("El tutor de prácticas no pertenece a la empresa seleccionada");
        }
        
        alumno.setEmpresa(empresa);
        alumno.setTutorPracticas(tutorPracticas);
        
        alumno = alumnoRepository.save(alumno);
        return convertToDTO(alumno);
    }
    
    // ==========================================================
    // --- IMPLEMENTACIÓN DE MÉTODOS ESTADÍSTICOS (NUEVOS) ---
    // ==========================================================
    
    /**
     * Calcula la nota media del alumno consultando el EvaluacionRepository.
     * Si no hay evaluaciones, retorna 0.0.
     * @param alumnoId ID del alumno.
     * @return Nota media calculada.
     */
    @Transactional(readOnly = true)
    public Double calcularNotaMedia(Long alumnoId) {
        // Asunción: EvaluacionRepository tiene un método para calcular el promedio por alumno.
        return evaluacionRepository.findAveragePuntuacionByAlumnoId(alumnoId).orElse(0.0);
    }

    /**
     * Cuenta el número total de evaluaciones realizadas al alumno.
     * @param alumnoId ID del alumno.
     * @return Número total de evaluaciones.
     */
    @Transactional(readOnly = true)
    public Long contarEvaluaciones(Long alumnoId) {
        // Asunción: EvaluacionRepository tiene un método para contar por alumno.
        return evaluacionRepository.countByAlumnoId(alumnoId);
    }

    /**
     * Cuenta el número total de observaciones diarias registradas por el alumno.
     * @param alumnoId ID del alumno.
     * @return Número total de observaciones.
     */
    @Transactional(readOnly = true)
    public Long contarObservaciones(Long alumnoId) {
        // Asunción: ObservacionDiariaRepository tiene un método para contar por alumno.
        return observacionDiariaRepository.countByAlumnoId(alumnoId);
    }

    // ==========================================================
    // --- MÉTODOS HELPER ---
    // ==========================================================
    
    // Método helper para validaciones
    private void validarDatosUnicos(String dni, String email, Long alumnoId) {
        // Validar DNI único
        alumnoRepository.findByDni(dni).ifPresent(a -> {
            if (alumnoId == null || !a.getId().equals(alumnoId)) {
                throw new DuplicateResourceException("Ya existe un alumno con el DNI: " + dni);
            }
        });
        
        // Validar email único
        alumnoRepository.findByEmail(email).ifPresent(a -> {
            if (alumnoId == null || !a.getId().equals(alumnoId)) {
                throw new DuplicateResourceException("Ya existe un alumno con el email: " + email);
            }
        });
    }
    
    // Método helper para convertir entidad a DTO
    private AlumnoDTO convertToDTO(Alumno alumno) {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setId(alumno.getId());
        
        // 💡 CORRECCIÓN APLICADA: Se verifica que el objeto Usuario no sea nulo antes de acceder a su ID.
        Usuario usuario = alumno.getUsuario();
        dto.setUsuarioId(usuario != null ? usuario.getId() : null); 
        
        dto.setNombre(alumno.getNombre());
        dto.setApellidos(alumno.getApellidos());
        dto.setDni(alumno.getDni());
        dto.setFechaNacimiento(alumno.getFechaNacimiento());
        dto.setEmail(alumno.getEmail());
        dto.setTelefono(alumno.getTelefono());
        dto.setContratado(alumno.getContratado());
        
        // Datos de relaciones
        if (alumno.getCurso() != null) {
            dto.setCursoId(alumno.getCurso().getId());
            dto.setCursoNombre(alumno.getCurso().getNombre());
        }
        
        if (alumno.getEmpresa() != null) {
            dto.setEmpresaId(alumno.getEmpresa().getId());
            dto.setEmpresaNombre(alumno.getEmpresa().getNombre());
        }
        
        if (alumno.getTutorPracticas() != null) {
            dto.setTutorPracticasId(alumno.getTutorPracticas().getId());
            dto.setTutorPracticasNombre(alumno.getTutorPracticas().getNombre() + " " + 
                                         alumno.getTutorPracticas().getApellidos());
        }

        // Faltaba el tutor de curso en el mapeo, lo añado para completar el DTO
        if (alumno.getTutorCurso() != null) {
            dto.setTutorCursoId(alumno.getTutorCurso().getId());
            dto.setTutorCursoNombre(alumno.getTutorCurso().getNombre() + " " + 
                                      alumno.getTutorCurso().getApellidos());
        }
        
        // Datos de prácticas
        dto.setDuracionPracticas(alumno.getDuracionPracticas());
        dto.setHorario(alumno.getHorario());
        dto.setFechaInicio(alumno.getFechaInicio()); 
        dto.setFechaFin(alumno.getFechaFin());
        
        // Metadata
        dto.setActivo(alumno.getActivo());
        dto.setFechaCreacion(alumno.getFechaCreacion());
        dto.setFechaActualizacion(alumno.getFechaActualizacion());
        
        return dto;
    }
}
