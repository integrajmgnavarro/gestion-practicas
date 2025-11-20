package com.gestionpracticas.services;

import com.gestionpracticas.dto.*;
import com.gestionpracticas.exception.ResourceNotFoundException;
import com.gestionpracticas.models.*;
import com.gestionpracticas.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de Spring Boot encargado de generar todos los informes
 * y métricas de la plataforma de gestión de prácticas.
 * Incluye lógica para reportes por Curso, Empresa, Tutor de Prácticas y Alumno,
 * además de un Reporte Ejecutivo.
 */
@Service
@RequiredArgsConstructor
public class ReportsService {

    private final AlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;
    private final EmpresaRepository empresaRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionTutorRepository evaluacionTutorRepository;
    private final ObservacionDiariaRepository observacionDiariaRepository;
    private final IncidenciaRepository incidenciaRepository;

    // ========================= REPORTES POR CURSO ========================= //

    @Transactional(readOnly = true)
    public ReporteCursoDTO getReporteCurso(Long cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con id: " + cursoId));

        ReporteCursoDTO reporte = new ReporteCursoDTO();
        
        // Datos del curso
        reporte.setCursoId(curso.getId());
        reporte.setCursoNombre(curso.getNombre());
        reporte.setDescripcion(curso.getDescripcion());
        reporte.setFechaInicio(curso.getFechaInicio());
        reporte.setFechaFin(curso.getFechaFin());
        
        if (curso.getTutorCurso() != null) {
            reporte.setTutorCursoNombre(curso.getTutorCurso().getNombre() + " " + curso.getTutorCurso().getApellidos());
        }
        
        // Alumnos del curso
        List<Alumno> alumnos = alumnoRepository.findByCurso_Id(cursoId);
        reporte.setTotalAlumnos(alumnos.size());
        reporte.setAlumnosActivos((int) alumnos.stream().filter(Alumno::getActivo).count());
        
        // Estadísticas de evaluación
        int evaluados = 0;
        int aprobados = 0;
        int sobresalientes = 0;
        int notables = 0;
        int bienes = 0;
        int suficientes = 0;
        int insuficientes = 0;
        BigDecimal sumaNotas = BigDecimal.ZERO;
        
        for (Alumno alumno : alumnos) {
            List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumno_Id(alumno.getId());
            if (!evaluaciones.isEmpty()) {
                evaluados++;
                BigDecimal nota = calcularNotaFinalAlumno(alumno.getId());
                sumaNotas = sumaNotas.add(nota);
                
                if (nota.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
                    aprobados++;
                }
                
                // Distribución
                if (nota.compareTo(BigDecimal.valueOf(9.0)) >= 0) {
                    sobresalientes++;
                } else if (nota.compareTo(BigDecimal.valueOf(7.0)) >= 0) {
                    notables++;
                } else if (nota.compareTo(BigDecimal.valueOf(6.0)) >= 0) {
                    bienes++;
                } else if (nota.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
                    suficientes++;
                } else {
                    insuficientes++;
                }
            }
        }
        
        reporte.setAlumnosEvaluados(evaluados);
        reporte.setAprobados(aprobados);
        reporte.setSuspendidos(evaluados - aprobados);
        
        if (evaluados > 0) {
            reporte.setNotaMedia(sumaNotas.divide(BigDecimal.valueOf(evaluados), 2, RoundingMode.HALF_UP));
            BigDecimal tasa = BigDecimal.valueOf(aprobados)
                    .divide(BigDecimal.valueOf(evaluados), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            reporte.setTasaAprobados(tasa);
        } else {
            reporte.setNotaMedia(BigDecimal.ZERO);
            reporte.setTasaAprobados(BigDecimal.ZERO);
        }
        
        reporte.setSobresalientes(sobresalientes);
        reporte.setNotables(notables);
        reporte.setBienes(bienes);
        reporte.setSuficientes(suficientes);
        reporte.setInsuficientes(insuficientes);
        
        // Empresas asociadas
        Set<String> empresas = alumnos.stream()
                .filter(a -> a.getEmpresa() != null)
                .map(a -> a.getEmpresa().getNombre())
                .collect(Collectors.toSet());
        reporte.setEmpresasAsociadas(new ArrayList<>(empresas));
        
        // Incidencias del curso
        int totalIncidencias = 0;
        long incidenciasAbiertas = 0;
        
        for (Alumno alumno : alumnos) {
            List<Incidencia> incidencias = incidenciaRepository.findByAlumno_Id(alumno.getId());
            totalIncidencias += incidencias.size();
            incidenciasAbiertas += incidencias.stream()
                    .filter(i -> Incidencia.EstadoIncidencia.ABIERTA.equals(i.getEstado()))
                    .count();
        }
        
        reporte.setTotalIncidencias(totalIncidencias);
        reporte.setIncidenciasAbiertas((int) incidenciasAbiertas);
        
        return reporte;
    }

    // ========================= REPORTES POR EMPRESA ========================= //

    @Transactional(readOnly = true)
    public ReporteEmpresaDTO getReporteEmpresa(Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id: " + empresaId));

        ReporteEmpresaDTO reporte = new ReporteEmpresaDTO();
        
        // Datos de la empresa
        reporte.setEmpresaId(empresa.getId());
        reporte.setEmpresaNombre(empresa.getNombre());
        reporte.setCif(empresa.getCif());
        reporte.setSector(empresa.getSector());
        reporte.setPersonaContacto(empresa.getPersonaContacto());
        
        // Tutores de la empresa
        List<TutorPracticas> tutores = tutorPracticasRepository.findByEmpresa_Id(empresaId);
        reporte.setTotalTutores(tutores.size());
        reporte.setNombresTutores(tutores.stream()
                .map(t -> t.getNombre() + " " + t.getApellidos())
                .collect(Collectors.toList()));
        
        // Alumnos de la empresa
        List<Alumno> alumnos = alumnoRepository.findByEmpresa_Id(empresaId);
        reporte.setTotalAlumnos(alumnos.size());
        reporte.setAlumnosActivos((int) alumnos.stream().filter(Alumno::getActivo).count());
        
        long finalizados = alumnos.stream()
                .filter(a -> a.getFechaFin() != null && 
                            (a.getFechaFin().isBefore(LocalDate.now()) || a.getFechaFin().isEqual(LocalDate.now())))
                .count();
        reporte.setAlumnosFinalizados((int) finalizados);
        
        long contratados = alumnos.stream()
                .filter(a -> a.getContratado() != null && a.getContratado())
                .count();
        reporte.setAlumnosContratados((int) contratados);
        
        // Evaluaciones
        int totalEvaluaciones = 0;
        BigDecimal sumaNotas = BigDecimal.ZERO;
        int evaluados = 0;
        
        for (Alumno alumno : alumnos) {
            List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumno_Id(alumno.getId());
            totalEvaluaciones += evaluaciones.size();
            if (!evaluaciones.isEmpty()) {
                evaluados++;
                sumaNotas = sumaNotas.add(calcularNotaFinalAlumno(alumno.getId()));
            }
        }
        
        reporte.setTotalEvaluaciones(totalEvaluaciones);
        if (evaluados > 0) {
            reporte.setNotaMediaAlumnos(sumaNotas.divide(BigDecimal.valueOf(evaluados), 2, RoundingMode.HALF_UP));
        } else {
            reporte.setNotaMediaAlumnos(BigDecimal.ZERO);
        }
        
        // Evaluaciones de tutores
        BigDecimal sumaEvalTutores = BigDecimal.ZERO;
        int totalEvalTutores = 0;
        
        for (TutorPracticas tutor : tutores) {
            List<EvaluacionTutor> evalsTutor = evaluacionTutorRepository.findByTutorPracticas(tutor);
            for (EvaluacionTutor eval : evalsTutor) {
                if (eval.getPuntuacion() != null) {
                    sumaEvalTutores = sumaEvalTutores.add(eval.getPuntuacion());
                    totalEvalTutores++;
                }
            }
        }
        
        if (totalEvalTutores > 0) {
            reporte.setEvaluacionMediaTutores(sumaEvalTutores.divide(BigDecimal.valueOf(totalEvalTutores), 2, RoundingMode.HALF_UP));
        } else {
            reporte.setEvaluacionMediaTutores(BigDecimal.ZERO);
        }
        
        // Incidencias
        int totalIncidencias = 0;
        long incidenciasResueltas = 0;
        
        for (Alumno alumno : alumnos) {
            List<Incidencia> incidencias = incidenciaRepository.findByAlumno_Id(alumno.getId());
            totalIncidencias += incidencias.size();
            incidenciasResueltas += incidencias.stream()
                    .filter(i -> Incidencia.EstadoIncidencia.RESUELTA.equals(i.getEstado()))
                    .count();
        }
        
        reporte.setTotalIncidencias(totalIncidencias);
        reporte.setIncidenciasResueltas((int) incidenciasResueltas);
        
        return reporte;
    }

    // ========================= REPORTES POR TUTOR DE PRÁCTICAS ========================= //

    @Transactional(readOnly = true)
    public ReporteTutorPracticasDTO getReporteTutorPracticas(Long tutorId) {
        TutorPracticas tutor = tutorPracticasRepository.findById(tutorId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor de prácticas no encontrado con id: " + tutorId));

        ReporteTutorPracticasDTO reporte = new ReporteTutorPracticasDTO();
        
        // Datos del tutor
        reporte.setTutorId(tutor.getId());
        reporte.setTutorNombre(tutor.getNombre() + " " + tutor.getApellidos());
        reporte.setCargo(tutor.getCargo());
        
        if (tutor.getEmpresa() != null) {
            reporte.setEmpresaNombre(tutor.getEmpresa().getNombre());
        }
        
        // Alumnos asignados
        List<Alumno> alumnos = alumnoRepository.findByTutorPracticas_Id(tutorId);
        reporte.setTotalAlumnos(alumnos.size());
        reporte.setAlumnosActivos((int) alumnos.stream().filter(Alumno::getActivo).count());
        reporte.setNombresAlumnos(alumnos.stream()
                .map(a -> a.getNombre() + " " + a.getApellidos())
                .collect(Collectors.toList()));
        
        // Evaluaciones realizadas por el tutor
        List<Evaluacion> evaluacionesRealizadas = evaluacionRepository.findByTutorPracticas_Id(tutorId);
        reporte.setEvaluacionesRealizadas(evaluacionesRealizadas.size());
        
        if (!evaluacionesRealizadas.isEmpty()) {
            BigDecimal sumaNotas = evaluacionesRealizadas.stream()
                    .filter(e -> e.getPuntuacion() != null)
                    .map(Evaluacion::getPuntuacion)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            long conNota = evaluacionesRealizadas.stream()
                    .filter(e -> e.getPuntuacion() != null)
                    .count();
            
            if (conNota > 0) {
                reporte.setNotaMediaOtorgada(sumaNotas.divide(BigDecimal.valueOf(conNota), 2, RoundingMode.HALF_UP));
            } else {
                reporte.setNotaMediaOtorgada(BigDecimal.ZERO);
            }
        } else {
            reporte.setNotaMediaOtorgada(BigDecimal.ZERO);
        }
        
        // Evaluaciones recibidas por el tutor
        List<EvaluacionTutor> evaluacionesRecibidas = evaluacionTutorRepository.findByTutorPracticas(tutor);
        reporte.setEvaluacionesRecibidas(evaluacionesRecibidas.size());
        
        if (!evaluacionesRecibidas.isEmpty()) {
            BigDecimal sumaNotas = evaluacionesRecibidas.stream()
                    .filter(e -> e.getPuntuacion() != null)
                    .map(EvaluacionTutor::getPuntuacion)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            long conNota = evaluacionesRecibidas.stream()
                    .filter(e -> e.getPuntuacion() != null)
                    .count();
            
            if (conNota > 0) {
                reporte.setNotaMediaRecibida(sumaNotas.divide(BigDecimal.valueOf(conNota), 2, RoundingMode.HALF_UP));
            } else {
                reporte.setNotaMediaRecibida(BigDecimal.ZERO);
            }
        } else {
            reporte.setNotaMediaRecibida(BigDecimal.ZERO);
        }
        
        // Observaciones e incidencias
        int totalObservaciones = 0;
        int totalIncidencias = 0;
        
        for (Alumno alumno : alumnos) {
            totalObservaciones += observacionDiariaRepository.countByAlumnoId(alumno.getId());
            totalIncidencias += incidenciaRepository.findByAlumno_Id(alumno.getId()).size();
        }
        
        reporte.setObservacionesRegistradas(totalObservaciones);
        reporte.setIncidenciasRegistradas(totalIncidencias);
        
        return reporte;
    }

    // ========================= REPORTES POR ALUMNO ========================= //

    @Transactional(readOnly = true)
    public ReporteAlumnoDTO getReporteAlumno(Long alumnoId) {
        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id: " + alumnoId));

        ReporteAlumnoDTO reporte = new ReporteAlumnoDTO();
        
        // Datos personales
        reporte.setAlumnoId(alumno.getId());
        reporte.setNombre(alumno.getNombre());
        reporte.setApellidos(alumno.getApellidos());
        reporte.setDni(alumno.getDni());
        reporte.setEmail(alumno.getEmail());
        reporte.setTelefono(alumno.getTelefono());
        
        // Datos académicos
        if (alumno.getCurso() != null) {
            reporte.setCursoNombre(alumno.getCurso().getNombre());
            if (alumno.getCurso().getTutorCurso() != null) {
                reporte.setTutorCursoNombre(alumno.getCurso().getTutorCurso().getNombre() + " " + 
                                           alumno.getCurso().getTutorCurso().getApellidos());
            }
        }
        
        if (alumno.getEmpresa() != null) {
            reporte.setEmpresaNombre(alumno.getEmpresa().getNombre());
        }
        
        if (alumno.getTutorPracticas() != null) {
            reporte.setTutorPracticasNombre(alumno.getTutorPracticas().getNombre() + " " + 
                                           alumno.getTutorPracticas().getApellidos());
        }
        
        // Prácticas
        reporte.setFechaInicio(alumno.getFechaInicio());
        reporte.setFechaFin(alumno.getFechaFin());
        reporte.setDuracionPracticas(alumno.getDuracionPracticas());
        reporte.setHorario(alumno.getHorario());
        reporte.setContratado(alumno.getContratado());
        
        // Evaluaciones
        List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumno_Id(alumnoId);
        reporte.setTotalEvaluaciones(evaluaciones.size());
        
        if (!evaluaciones.isEmpty()) {
            BigDecimal notaFinal = calcularNotaFinalAlumno(alumnoId);
            reporte.setNotaFinal(notaFinal);
            reporte.setCalificacion(obtenerCalificacionTexto(notaFinal));
            
            // Detalle de evaluaciones
            List<EvaluacionDetalleDTO> detalles = evaluaciones.stream()
                    .map(this::convertToDetalleDTO)
                    .collect(Collectors.toList());
            reporte.setDetalleEvaluaciones(detalles);
        } else {
            reporte.setNotaFinal(BigDecimal.ZERO);
            reporte.setCalificacion("Sin evaluar");
            reporte.setDetalleEvaluaciones(new ArrayList<>());
        }
        
        // Observaciones
        long totalObservaciones = observacionDiariaRepository.countByAlumnoId(alumnoId);
        reporte.setTotalObservaciones((int) totalObservaciones);
        
        List<ObservacionDiaria> observaciones = observacionDiariaRepository.findByAlumno_Id(alumnoId);
        int horasTotales = observaciones.stream()
                .filter(o -> o.getHorasRealizadas() != null)
                .mapToInt(ObservacionDiaria::getHorasRealizadas)
                .sum();
        reporte.setHorasTotales(horasTotales);
        
        // Incidencias
        List<Incidencia> incidencias = incidenciaRepository.findByAlumno_Id(alumnoId);
        reporte.setTotalIncidencias(incidencias.size());
        reporte.setIncidenciasAbiertas((int) incidencias.stream()
                .filter(i -> Incidencia.EstadoIncidencia.ABIERTA.equals(i.getEstado()))
                .count());
        
        return reporte;
    }

    // ========================= REPORTE EJECUTIVO ========================= //

    @Transactional(readOnly = true)
    public ReporteEjecutivoDTO getReporteEjecutivo() {
        ReporteEjecutivoDTO reporte = new ReporteEjecutivoDTO();
        
        // KPIs generales
        reporte.setTotalAlumnos((int) alumnoRepository.count());
        reporte.setTotalCursos((int) cursoRepository.count());
        reporte.setTotalEmpresas((int) empresaRepository.count());
        reporte.setTotalTutoresPracticas((int) tutorPracticasRepository.count());
        
        // Evaluaciones
        List<Alumno> alumnos = alumnoRepository.findAll();
        int totalEvaluaciones = 0;
        int evaluados = 0;
        int aprobados = 0;
        BigDecimal sumaNotas = BigDecimal.ZERO;
        
        for (Alumno alumno : alumnos) {
            List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumno_Id(alumno.getId());
            totalEvaluaciones += evaluaciones.size();
            
            if (!evaluaciones.isEmpty()) {
                evaluados++;
                BigDecimal nota = calcularNotaFinalAlumno(alumno.getId());
                sumaNotas = sumaNotas.add(nota);
                
                if (nota.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
                    aprobados++;
                }
            }
        }
        
        reporte.setTotalEvaluaciones(totalEvaluaciones);
        
        if (evaluados > 0) {
            reporte.setNotaMediaGlobal(sumaNotas.divide(BigDecimal.valueOf(evaluados), 2, RoundingMode.HALF_UP));
            BigDecimal tasa = BigDecimal.valueOf(aprobados)
                    .divide(BigDecimal.valueOf(evaluados), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            reporte.setTasaAprobadosGlobal(tasa);
        } else {
            reporte.setNotaMediaGlobal(BigDecimal.ZERO);
            reporte.setTasaAprobadosGlobal(BigDecimal.ZERO);
        }
        
        // Empleabilidad
        List<Alumno> alumnosFinalizados = alumnos.stream()
                .filter(a -> a.getFechaFin() != null && 
                            (a.getFechaFin().isBefore(LocalDate.now()) || a.getFechaFin().isEqual(LocalDate.now())))
                .collect(Collectors.toList());
        
        reporte.setAlumnosFinalizados(alumnosFinalizados.size());
        
        long contratados = alumnosFinalizados.stream()
                .filter(a -> a.getContratado() != null && a.getContratado())
                .count();
        
        reporte.setAlumnosContratados((int) contratados);
        
        if (!alumnosFinalizados.isEmpty()) {
            BigDecimal porcentaje = BigDecimal.valueOf(contratados)
                    .divide(BigDecimal.valueOf(alumnosFinalizados.size()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            reporte.setPorcentajeContratacion(porcentaje);
        } else {
            reporte.setPorcentajeContratacion(BigDecimal.ZERO);
        }
        
        // Incidencias
        List<Incidencia> incidencias = incidenciaRepository.findAll();
        reporte.setTotalIncidencias(incidencias.size());
        reporte.setIncidenciasAbiertas((int) incidencias.stream()
                .filter(i -> Incidencia.EstadoIncidencia.ABIERTA.equals(i.getEstado()))
                .count());
        reporte.setIncidenciasResueltas((int) incidencias.stream()
                .filter(i -> Incidencia.EstadoIncidencia.RESUELTA.equals(i.getEstado()))
                .count());
        
        // Observaciones
        long totalObservaciones = observacionDiariaRepository.count();
        reporte.setTotalObservaciones((int) totalObservaciones);
        
        List<ObservacionDiaria> observaciones = observacionDiariaRepository.findAll();
        if (!observaciones.isEmpty()) {
            int totalHoras = observaciones.stream()
                    .filter(o -> o.getHorasRealizadas() != null)
                    .mapToInt(ObservacionDiaria::getHorasRealizadas)
                    .sum();
            
            long conHoras = observaciones.stream()
                    .filter(o -> o.getHorasRealizadas() != null)
                    .count();
            
            if (conHoras > 0) {
                reporte.setPromedioHorasDiarias(
                        BigDecimal.valueOf(totalHoras)
                                .divide(BigDecimal.valueOf(conHoras), 2, RoundingMode.HALF_UP));
            } else {
                reporte.setPromedioHorasDiarias(BigDecimal.ZERO);
            }
        } else {
            reporte.setPromedioHorasDiarias(BigDecimal.ZERO);
        }
        
        return reporte;
    }

    // ========================= MÉTODOS AUXILIARES ========================= //

    /**
     * Calcula la nota final de un alumno basándose en todas sus evaluaciones
     * y los pesos de los criterios de evaluación.
     * @param alumnoId El ID del alumno.
     * @return La nota final ponderada.
     */
    private BigDecimal calcularNotaFinalAlumno(Long alumnoId) {
        List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumno_Id(alumnoId);
        
        if (evaluaciones.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal notaTotal = BigDecimal.ZERO;
        BigDecimal pesoTotal = BigDecimal.ZERO;

        for (Evaluacion eval : evaluaciones) {
            // Se asume que getCapacidad() y getCriterio() nunca son null aquí para simplificar
            // el ejemplo, pero en producción deberíamos añadir comprobaciones.
            CriterioEvaluacion criterio = eval.getCapacidad().getCriterio();
            BigDecimal peso = criterio.getPeso();
            
            // Normaliza la puntuación obtenida a una base 10 (asumiendo que PuntuacionMaxima es la base)
            BigDecimal puntuacionNormalizada = eval.getPuntuacion()
                    .divide(BigDecimal.valueOf(eval.getCapacidad().getPuntuacionMaxima()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.TEN);
            
            // Calcula la contribución de esta evaluación a la nota final ponderada
            BigDecimal contribucion = puntuacionNormalizada
                    .multiply(peso)
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                    
            notaTotal = notaTotal.add(contribucion);
            pesoTotal = pesoTotal.add(peso);
        }

        if (pesoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        // En este modelo, la notaTotal ya es la nota final ponderada.
        // Se escala a 2 decimales para la presentación.
        return notaTotal.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Convierte la nota numérica en una calificación textual (Sobresaliente, Notable, etc.).
     * @param nota La nota numérica.
     * @return La calificación textual.
     */
    private String obtenerCalificacionTexto(BigDecimal nota) {
        if (nota.compareTo(BigDecimal.valueOf(9.0)) >= 0) {
            return "Sobresaliente";
        } else if (nota.compareTo(BigDecimal.valueOf(7.0)) >= 0) {
            return "Notable";
        } else if (nota.compareTo(BigDecimal.valueOf(6.0)) >= 0) {
            return "Bien";
        } else if (nota.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
            return "Suficiente";
        } else {
            return "Insuficiente";
        }
    }

    /**
     * Convierte un objeto Evaluacion a su DTO de detalle para el reporte de alumno.
     * @param evaluacion La entidad Evaluacion.
     * @return El DTO de detalle.
     */
    private EvaluacionDetalleDTO convertToDetalleDTO(Evaluacion evaluacion) {
        EvaluacionDetalleDTO dto = new EvaluacionDetalleDTO();
        
        if (evaluacion.getCapacidad() != null) {
            dto.setCapacidadNombre(evaluacion.getCapacidad().getNombre());
            dto.setPuntuacionMaxima(evaluacion.getCapacidad().getPuntuacionMaxima());
            
            if (evaluacion.getCapacidad().getCriterio() != null) {
                dto.setCriterioNombre(evaluacion.getCapacidad().getCriterio().getNombre());
            }
        }
        
        dto.setPuntuacion(evaluacion.getPuntuacion());
        dto.setFecha(evaluacion.getFecha());
        dto.setObservaciones(evaluacion.getObservaciones());
        
        return dto;
    }
}
