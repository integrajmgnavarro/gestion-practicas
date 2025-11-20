package com.gestionpracticas.services;

import com.gestionpracticas.dto.AlumnoCreateDTO;
import com.gestionpracticas.dto.AlumnoDTO;
import com.gestionpracticas.dto.AlumnoUpdateDTO;
import com.gestionpracticas.dto.AlumnoSearchDTO; // Importar el DTO de búsqueda
import com.gestionpracticas.exception.DuplicateResourceException;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.models.Alumno;
import com.gestionpracticas.models.Curso;
import com.gestionpracticas.models.Empresa;
import com.gestionpracticas.models.TutorPracticas;
import com.gestionpracticas.models.TutorCurso;
import com.gestionpracticas.repositories.AlumnoRepository;
import com.gestionpracticas.repositories.CursoRepository;
import com.gestionpracticas.repositories.EmpresaRepository;
import com.gestionpracticas.repositories.TutorPracticasRepository;
import com.gestionpracticas.repositories.TutorCursoRepository;

// Imports de Spring y Java
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de alumnos.
 * Contiene operaciones CRUD, búsqueda y manejo de relaciones.
 * Incluye métodos para filtrar por Tutor de Prácticas y Tutor de Curso.
 */
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
     * Convierte una entidad Alumno a un DTO de actualización (AlumnoUpdateDTO)
     * para pre-cargar formularios de edición.
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
        dto.setActivo(alumno.getActivo());

        // Datos de Prácticas
        dto.setDuracionPracticas(alumno.getDuracionPracticas());
        dto.setHorario(alumno.getHorario());
        dto.setFechaInicio(alumno.getFechaInicio());
        dto.setFechaFin(alumno.getFechaFin());
        dto.setContratado(alumno.getContratado());

        // Relaciones (Asignar IDs, manejar nulos)
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
    // --- LÓGICA DE NEGOCIO: LISTADO Y BÚSQUEDA ---
    // ==========================================================

    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAllAlumnos() {
        return alumnoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAlumnosActivos() {
        // Se asume la existencia de findByActivo en AlumnoRepository
        return alumnoRepository.findByActivo(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los alumnos asignados a un tutor de prácticas específico.
     * @param tutorPracticasId ID del Tutor de Prácticas.
     * @return Lista de AlumnoDTOs.
     */
    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAlumnosByTutorPracticas(Long tutorPracticasId) {
        // Se asume la existencia de findByTutorPracticasId en AlumnoRepository.
        return alumnoRepository.findByTutorPracticas_Id(tutorPracticasId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * NUEVO MÉTODO REQUERIDO: Obtiene todos los alumnos asignados a un tutor de curso específico.
     * Es esencial para que el TutorCursoController solo acceda a los alumnos de su(s) curso(s) asignado(s).
     * @param tutorCursoId ID del Tutor de Curso.
     * @return Lista de AlumnoDTOs.
     */
    @Transactional(readOnly = true)
    public List<AlumnoDTO> getAlumnosByTutorCurso(Long tutorCursoId) {
        // Se asume la existencia de findByTutorCursoId en AlumnoRepository.
        return alumnoRepository.findByTutorCurso_Id(tutorCursoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * BÚSQUEDA GENERAL.
     * Busca alumnos cuyo nombre, apellidos o DNI contenga el término de búsqueda.
     * @param searchDTO El DTO que contiene el término de búsqueda.
     * @return Lista de AlumnoDTOs que coinciden.
     */
    @Transactional(readOnly = true)
    public List<AlumnoDTO> searchAlumnos(AlumnoSearchDTO searchDTO) {
        // 1. Extraer el término de búsqueda (asumiendo que el DTO tiene un método getSearchTerm())
        String searchTerm = searchDTO.getSearchTerm();

        // 2. Si el término está vacío o nulo, devolver una lista vacía o todos los alumnos.
        // Aquí elegimos devolver una lista vacía si no hay término.
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of(); 
        }

        // 3. Llamar al método del repositorio con la cadena extraída
        List<Alumno> alumnos = alumnoRepository.findBySearchTerm(searchTerm);
        
        // 4. Mapear y devolver
        return alumnos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * BÚSQUEDA CON PAGINACIÓN.
     * Busca alumnos aplicando múltiples criterios de filtrado y paginación.
     */
    @Transactional(readOnly = true)
    public Page<AlumnoDTO> findAlumnosByCriteria(
            String nombre,
            String apellidos,
            String dni,
            Long cursoId,
            Long empresaId,
            Boolean activo,
            Pageable pageable) {

        // Nota: Se asume que AlumnoRepository tiene un método personalizado
        // llamado findByMultipleCriteriaWithPagination.
        Page<Alumno> alumnoPage = alumnoRepository.findByMultipleCriteriaWithPagination(
                nombre,
                apellidos,
                dni,
                cursoId,
                empresaId,
                activo,
                pageable
        );

        // Mapea el Page de Entidades a un Page de DTOs
        return alumnoPage.map(this::convertToDTO);
    }


    // ==========================================================
    // --- LÓGICA DE NEGOCIO: CRUD INDIVIDUAL ---
    // ==========================================================

    @Transactional(readOnly = true)
    public AlumnoDTO getAlumnoById(Long id) {
        Alumno alumno = getAlumnoEntityById(id);
        return convertToDTO(alumno);
    }

    /**
     * Busca la entidad Alumno. Utilizado internamente.
     */
    @Transactional(readOnly = true)
    public Alumno getAlumnoEntityById(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id: " + id));
    }

    /**
     * BUSQUEDA DE DTO DE EDICIÓN
     * @param id ID del alumno.
     * @return El DTO de actualización.
     */
    @Transactional(readOnly = true)
    public AlumnoUpdateDTO findAlumnoUpdateDTOById(Long id) throws ResourceNotFoundException {
        Alumno alumno = getAlumnoEntityById(id);
        return toUpdateDTO(alumno);
    }

    @Transactional
    public AlumnoDTO createAlumno(AlumnoCreateDTO createDTO) {
        // 1. Validar unicidad de DNI y Email
        validarDatosUnicos(createDTO.getDni(), createDTO.getEmail(), null);

        // 2. Mapear DTO a Entidad
        Alumno alumno = new Alumno();
        alumno.setNombre(createDTO.getNombre());
        alumno.setApellidos(createDTO.getApellidos());
        alumno.setDni(createDTO.getDni());
        alumno.setEmail(createDTO.getEmail());
        alumno.setTelefono(createDTO.getTelefono());
        alumno.setFechaNacimiento(createDTO.getFechaNacimiento());
        alumno.setContratado(false);
        alumno.setActivo(true);

        // 3. Establecer Relaciones (Obligatorio: Curso)
        Curso curso = cursoRepository.findById(createDTO.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + createDTO.getCursoId()));
        alumno.setCurso(curso);

        // Relaciones Opcionales
        alumno.setEmpresa(
            findOptionalEntity(empresaRepository, createDTO.getEmpresaId(), "Empresa")
                .orElse(null)
        );
        alumno.setTutorPracticas(
            findOptionalEntity(tutorPracticasRepository, createDTO.getTutorPracticasId(), "Tutor de Prácticas")
                .orElse(null)
        );
        alumno.setTutorCurso(
            findOptionalEntity(tutorCursoRepository, createDTO.getTutorCursoId(), "Tutor de Curso")
                .orElse(null)
        );

        // Datos de Prácticas
        alumno.setDuracionPracticas(createDTO.getDuracionPracticas());
        alumno.setHorario(createDTO.getHorario());
        alumno.setFechaInicio(createDTO.getFechaInicio());
        alumno.setFechaFin(createDTO.getFechaFin());

        // 4. Guardar y devolver DTO
        Alumno savedAlumno = alumnoRepository.save(alumno);
        return convertToDTO(savedAlumno);
    }

    @Transactional
    public AlumnoDTO updateAlumno(AlumnoUpdateDTO dto) {
        Alumno alumno = alumnoRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con ID: " + dto.getId()));

        // 1. Validar unicidad de DNI y Email
        validarDatosUnicos(dto.getDni(), dto.getEmail(), dto.getId());

        // 2. Actualización de campos personales y de estado
        alumno.setNombre(dto.getNombre());
        alumno.setApellidos(dto.getApellidos());
        alumno.setDni(dto.getDni());
        alumno.setEmail(dto.getEmail());
        alumno.setTelefono(dto.getTelefono());
        alumno.setFechaNacimiento(dto.getFechaNacimiento());
        alumno.setActivo(dto.getActivo());

        // 3. Actualización de datos de prácticas
        alumno.setDuracionPracticas(dto.getDuracionPracticas());
        alumno.setHorario(dto.getHorario());
        alumno.setFechaInicio(dto.getFechaInicio());
        alumno.setFechaFin(dto.getFechaFin());
        alumno.setContratado(dto.getContratado());


        // 4. Actualización de Relaciones

        // Curso (Obligatorio)
        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + dto.getCursoId()));
        alumno.setCurso(curso);

        // Empresa (Opcional)
        alumno.setEmpresa(
            findOptionalEntity(empresaRepository, dto.getEmpresaId(), "Empresa")
                .orElse(null)
        );

        // Tutor de Prácticas (Opcional)
        alumno.setTutorPracticas(
            findOptionalEntity(tutorPracticasRepository, dto.getTutorPracticasId(), "Tutor de Prácticas")
                .orElse(null)
        );

        // Tutor de Curso (Opcional)
        alumno.setTutorCurso(
            findOptionalEntity(tutorCursoRepository, dto.getTutorCursoId(), "Tutor de Curso")
                .orElse(null)
        );

        // 5. Guardar y devolver DTO
        Alumno updatedAlumno = alumnoRepository.save(alumno);
        return convertToDTO(updatedAlumno);
    }

    @Transactional
    public void deleteAlumno(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado para eliminar con ID: " + id));

        alumnoRepository.delete(alumno);
    }

    // ==========================================================
    // --- MÉTODOS AUXILIARES ---
    // ==========================================================

    /**
     * Método genérico para buscar una entidad opcional por ID utilizando JpaRepository.
     * Si el ID es nulo, devuelve Optional.empty().
     * Si el ID no se encuentra, lanza ResourceNotFoundException.
     * @param repository El repositorio de la entidad.
     * @param id El ID a buscar.
     * @param entityName Nombre de la entidad para mensajes de error.
     * @return Optional con la entidad encontrada o vacío.
     */
    private <T> Optional<T> findOptionalEntity(JpaRepository<T, Long> repository, Long id, String entityName) {
        if (id == null) {
            return Optional.empty();
        }
        // orElseThrow() con función lambda para crear la excepción solo si es necesario (findById devuelve Optional)
        return repository.findById(id).or(() -> {
            throw new ResourceNotFoundException(entityName + " no encontrado con ID: " + id);
        });
    }

    /**
     * Valida que el DNI y el Email no estén ya registrados por otro alumno.
     */
    private void validarDatosUnicos(String dni, String email, Long alumnoId) {
        // Se asume la existencia de findByDni y findByEmail en AlumnoRepository
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
        dto.setDuracionPracticas(alumno.getDuracionPracticas());
        dto.setHorario(alumno.getHorario());
        dto.setFechaInicio(alumno.getFechaInicio());
        dto.setFechaFin(alumno.getFechaFin());
        dto.setContratado(alumno.getContratado());
        dto.setActivo(alumno.getActivo());

        // Mapeo de IDs y nombres de relaciones
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
        }
        if (alumno.getTutorCurso() != null) {
            dto.setTutorCursoId(alumno.getTutorCurso().getId());
        }

        return dto;
    }
}