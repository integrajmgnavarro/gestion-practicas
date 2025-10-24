package com.gestionpracticas.services;

import com.gestionpracticas.dto.*;
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

@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final AlumnoRepository alumnoRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionTutorRepository evaluacionTutorRepository;
    private final CursoRepository cursoRepository;
    private final EmpresaRepository empresaRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final ObservacionDiariaRepository observacionDiariaRepository;
    private final IncidenciaRepository incidenciaRepository;

    // ========================= ESTADÍSTICAS GENERALES ========================= //

    @Transactional(readOnly = true)
    public EstadisticasGeneralesDTO getEstadisticasGenerales() {
        EstadisticasGeneralesDTO stats = new EstadisticasGeneralesDTO();
        
        // Contadores básicos
        stats.setTotalAlumnos(alumnoRepository.count());
        stats.setTotalEmpresas(empresaRepository.count());
        stats.setTotalCursos(cursoRepository.count());
        stats.setTotalTutoresPracticas(tutorPracticasRepository.count());
        
        // Alumnos activos
        stats.setAlumnosActivos(alumnoRepository.countByActivoTrue());
        
        // Alumnos en prácticas (con fechas válidas)
        stats.setAlumnosEnPracticas(alumnoRepository.countByFechaInicioIsNotNullAndFechaFinIsNotNullAndActivoTrue());
        
        return stats;
    }

    @Transactional(readOnly = true)
    public EstadisticasAprobadosDTO getEstadisticasAprobados() {
        EstadisticasAprobadosDTO stats = new EstadisticasAprobadosDTO();
        
        List<Alumno> alumnos = alumnoRepository.findAll();
        int totalEvaluados = 0;
        int aprobados = 0;
        
        for (Alumno alumno : alumnos) {
            List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumnoId(alumno.getId());
            if (!evaluaciones.isEmpty()) {
                totalEvaluados++;
                BigDecimal notaFinal = calcularNotaFinalAlumno(alumno.getId());
                if (notaFinal.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
                    aprobados++;
                }
            }
        }
        
        stats.setTotalEvaluados(totalEvaluados);
        stats.setAprobados(aprobados);
        stats.setSuspendidos(totalEvaluados - aprobados);
        
        if (totalEvaluados > 0) {
            BigDecimal porcentaje = BigDecimal.valueOf(aprobados)
                    .divide(BigDecimal.valueOf(totalEvaluados), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            stats.setPorcentajeAprobados(porcentaje.setScale(2, RoundingMode.HALF_UP));
        } else {
            stats.setPorcentajeAprobados(BigDecimal.ZERO);
        }
        
        return stats;
    }

    @Transactional(readOnly = true)
    public List<EstadisticasPorCursoDTO> getEstadisticasPorCurso() {
        List<EstadisticasPorCursoDTO> estadisticas = new ArrayList<>();
        List<Curso> cursos = cursoRepository.findAll();
        
        for (Curso curso : cursos) {
            EstadisticasPorCursoDTO stats = new EstadisticasPorCursoDTO();
            stats.setCursoId(curso.getId());
            stats.setCursoNombre(curso.getNombre());
            
            List<Alumno> alumnos = alumnoRepository.findByCursoId(curso.getId());
            stats.setTotalAlumnos(alumnos.size());
            
            int evaluados = 0;
            int aprobados = 0;
            BigDecimal sumaNotas = BigDecimal.ZERO;
            
            for (Alumno alumno : alumnos) {
                List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumnoId(alumno.getId());
                if (!evaluaciones.isEmpty()) {
                    evaluados++;
                    BigDecimal nota = calcularNotaFinalAlumno(alumno.getId());
                    sumaNotas = sumaNotas.add(nota);
                    if (nota.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
                        aprobados++;
                    }
                }
            }
            
            stats.setAlumnosEvaluados(evaluados);
            stats.setAprobados(aprobados);
            stats.setSuspendidos(evaluados - aprobados);
            
            if (evaluados > 0) {
                stats.setNotaMedia(sumaNotas.divide(BigDecimal.valueOf(evaluados), 2, RoundingMode.HALF_UP));
                BigDecimal porcentaje = BigDecimal.valueOf(aprobados)
                        .divide(BigDecimal.valueOf(evaluados), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                stats.setPorcentajeAprobados(porcentaje.setScale(2, RoundingMode.HALF_UP));
            } else {
                stats.setNotaMedia(BigDecimal.ZERO);
                stats.setPorcentajeAprobados(BigDecimal.ZERO);
            }
            
            estadisticas.add(stats);
        }
        
        return estadisticas;
    }

    @Transactional(readOnly = true)
    public List<EstadisticasPorEmpresaDTO> getEstadisticasPorEmpresa() {
        List<EstadisticasPorEmpresaDTO> estadisticas = new ArrayList<>();
        List<Empresa> empresas = empresaRepository.findAll();
        
        for (Empresa empresa : empresas) {
            EstadisticasPorEmpresaDTO stats = new EstadisticasPorEmpresaDTO();
            stats.setEmpresaId(empresa.getId());
            stats.setEmpresaNombre(empresa.getNombre());
            
            List<Alumno> alumnos = alumnoRepository.findByEmpresaId(empresa.getId());
            stats.setTotalAlumnos(alumnos.size());
            
            int evaluados = 0;
            int aprobados = 0;
            BigDecimal sumaNotas = BigDecimal.ZERO;
            
            for (Alumno alumno : alumnos) {
                List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumnoId(alumno.getId());
                if (!evaluaciones.isEmpty()) {
                    evaluados++;
                    BigDecimal nota = calcularNotaFinalAlumno(alumno.getId());
                    sumaNotas = sumaNotas.add(nota);
                    if (nota.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
                        aprobados++;
                    }
                }
            }
            
            stats.setAlumnosEvaluados(evaluados);
            stats.setAprobados(aprobados);
            stats.setSuspendidos(evaluados - aprobados);
            
            if (evaluados > 0) {
                stats.setNotaMedia(sumaNotas.divide(BigDecimal.valueOf(evaluados), 2, RoundingMode.HALF_UP));
            } else {
                stats.setNotaMedia(BigDecimal.ZERO);
            }
            
            // Contar tutores de la empresa
            stats.setTotalTutores(tutorPracticasRepository.countByEmpresaId(empresa.getId()));
            
            estadisticas.add(stats);
        }
        
        return estadisticas;
    }

    @Transactional(readOnly = true)
    public List<EstadisticasPorTutorDTO> getEstadisticasPorTutor() {
        List<EstadisticasPorTutorDTO> estadisticas = new ArrayList<>();
        List<TutorPracticas> tutores = tutorPracticasRepository.findAll();
        
        for (TutorPracticas tutor : tutores) {
            EstadisticasPorTutorDTO stats = new EstadisticasPorTutorDTO();
            stats.setTutorId(tutor.getId());
            stats.setTutorNombre(tutor.getNombre() + " " + tutor.getApellidos());
            
            if (tutor.getEmpresa() != null) {
                stats.setEmpresaNombre(tutor.getEmpresa().getNombre());
            }
            
            List<Alumno> alumnos = alumnoRepository.findByTutorPracticasId(tutor.getId());
            stats.setTotalAlumnos(alumnos.size());
            
            int evaluados = 0;
            int aprobados = 0;
            BigDecimal sumaNotas = BigDecimal.ZERO;
            
            for (Alumno alumno : alumnos) {
                List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumnoId(alumno.getId());
                if (!evaluaciones.isEmpty()) {
                    evaluados++;
                    BigDecimal nota = calcularNotaFinalAlumno(alumno.getId());
                    sumaNotas = sumaNotas.add(nota);
                    if (nota.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
                        aprobados++;
                    }
                }
            }
            
            stats.setAlumnosEvaluados(evaluados);
            stats.setAprobados(aprobados);
            
            if (evaluados > 0) {
                stats.setNotaMedia(sumaNotas.divide(BigDecimal.valueOf(evaluados), 2, RoundingMode.HALF_UP));
            } else {
                stats.setNotaMedia(BigDecimal.ZERO);
            }
            
            // Promedio de evaluaciones del tutor
            List<EvaluacionTutor> evaluacionesTutor = evaluacionTutorRepository.findByTutorPracticasId(tutor.getId());
            if (!evaluacionesTutor.isEmpty()) {
                BigDecimal sumaEvaluaciones = evaluacionesTutor.stream()
                        .map(EvaluacionTutor::getPuntuacion)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                stats.setEvaluacionPromedio(sumaEvaluaciones.divide(
                        BigDecimal.valueOf(evaluacionesTutor.size()), 2, RoundingMode.HALF_UP));
            } else {
                stats.setEvaluacionPromedio(BigDecimal.ZERO);
            }
            
            estadisticas.add(stats);
        }
        
        return estadisticas;
    }

    // ========================= DISTRIBUCIÓN DE CALIFICACIONES ========================= //

    @Transactional(readOnly = true)
    public DistribucionCalificacionesDTO getDistribucionCalificaciones() {
        DistribucionCalificacionesDTO distribucion = new DistribucionCalificacionesDTO();
        
        List<Alumno> alumnos = alumnoRepository.findAll();
        int sobresaliente = 0; // >= 9
        int notable = 0;        // >= 7 y < 9
        int bien = 0;           // >= 6 y < 7
        int suficiente = 0;     // >= 5 y < 6
        int insuficiente = 0;   // < 5
        
        for (Alumno alumno : alumnos) {
            List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumnoId(alumno.getId());
            if (!evaluaciones.isEmpty()) {
                BigDecimal nota = calcularNotaFinalAlumno(alumno.getId());
                
                if (nota.compareTo(BigDecimal.valueOf(9.0)) >= 0) {
                    sobresaliente++;
                } else if (nota.compareTo(BigDecimal.valueOf(7.0)) >= 0) {
                    notable++;
                } else if (nota.compareTo(BigDecimal.valueOf(6.0)) >= 0) {
                    bien++;
                } else if (nota.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
                    suficiente++;
                } else {
                    insuficiente++;
                }
            }
        }
        
        distribucion.setSobresaliente(sobresaliente);
        distribucion.setNotable(notable);
        distribucion.setBien(bien);
        distribucion.setSuficiente(suficiente);
        distribucion.setInsuficiente(insuficiente);
        
        return distribucion;
    }

    // ========================= ESTADÍSTICAS DE DURACIÓN ========================= //

    @Transactional(readOnly = true)
    public EstadisticasDuracionDTO getEstadisticasDuracion() {
        EstadisticasDuracionDTO stats = new EstadisticasDuracionDTO();
        
        List<Alumno> alumnos = alumnoRepository.findByFechaInicioIsNotNullAndFechaFinIsNotNull();
        
        if (alumnos.isEmpty()) {
            stats.setDuracionMedia(0);
            stats.setDuracionMinima(0);
            stats.setDuracionMaxima(0);
            return stats;
        }
        
        List<Integer> duraciones = new ArrayList<>();
        
        for (Alumno alumno : alumnos) {
            if (alumno.getDuracionPracticas() != null) {
                duraciones.add(alumno.getDuracionPracticas());
            }
        }
        
        if (!duraciones.isEmpty()) {
            double media = duraciones.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
            
            stats.setDuracionMedia((int) Math.round(media));
            stats.setDuracionMinima(Collections.min(duraciones));
            stats.setDuracionMaxima(Collections.max(duraciones));
        } else {
            stats.setDuracionMedia(0);
            stats.setDuracionMinima(0);
            stats.setDuracionMaxima(0);
        }
        
        return stats;
    }

    // ========================= ESTADÍSTICAS DE INCIDENCIAS ========================= //

    @Transactional(readOnly = true)
    public EstadisticasIncidenciasDTO getEstadisticasIncidencias() {
        EstadisticasIncidenciasDTO stats = new EstadisticasIncidenciasDTO();
        
        List<Incidencia> incidencias = incidenciaRepository.findAll();
        
        stats.setTotalIncidencias(incidencias.size());
        stats.setIncidenciasAbiertas(incidenciaRepository.countByEstado("ABIERTA"));
        stats.setIncidenciasEnProceso(incidenciaRepository.countByEstado("EN_PROCESO"));
        stats.setIncidenciasResueltas(incidenciaRepository.countByEstado("RESUELTA"));
        
        // Incidencias por tipo
        Map<String, Long> porTipo = incidencias.stream()
                .collect(Collectors.groupingBy(Incidencia::getTipo, Collectors.counting()));
        
        stats.setIncidenciasPorTipo(porTipo);
        
        return stats;
    }

    // ========================= ESTADÍSTICAS DE OBSERVACIONES ========================= //

    @Transactional(readOnly = true)
    public EstadisticasObservacionesDTO getEstadisticasObservaciones() {
        EstadisticasObservacionesDTO stats = new EstadisticasObservacionesDTO();
        
        stats.setTotalObservaciones(observacionDiariaRepository.count());
        
        // Observaciones del mes actual
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        
        stats.setObservacionesMesActual(
                observacionDiariaRepository.countByFechaBetween(inicioMes, finMes));
        
        // Promedio de horas trabajadas
        List<ObservacionDiaria> observaciones = observacionDiariaRepository.findAll();
        
        if (!observaciones.isEmpty()) {
            int totalHoras = observaciones.stream()
                    .filter(obs -> obs.getHorasRealizadas() != null)
                    .mapToInt(ObservacionDiaria::getHorasRealizadas)
                    .sum();
            
            long observacionesConHoras = observaciones.stream()
                    .filter(obs -> obs.getHorasRealizadas() != null)
                    .count();
            
            if (observacionesConHoras > 0) {
                stats.setPromedioHorasDiarias(
                        BigDecimal.valueOf(totalHoras)
                                .divide(BigDecimal.valueOf(observacionesConHoras), 2, RoundingMode.HALF_UP));
            } else {
                stats.setPromedioHorasDiarias(BigDecimal.ZERO);
            }
        } else {
            stats.setPromedioHorasDiarias(BigDecimal.ZERO);
        }
        
        return stats;
    }

    // ========================= RANKING DE ALUMNOS ========================= //

    @Transactional(readOnly = true)
    public List<RankingAlumnoDTO> getRankingAlumnos(Integer limite) {
        List<Alumno> alumnos = alumnoRepository.findAll();
        List<RankingAlumnoDTO> ranking = new ArrayList<>();
        
        for (Alumno alumno : alumnos) {
            List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumnoId(alumno.getId());
            if (!evaluaciones.isEmpty()) {
                RankingAlumnoDTO item = new RankingAlumnoDTO();
                item.setAlumnoId(alumno.getId());
                item.setAlumnoNombre(alumno.getNombre() + " " + alumno.getApellidos());
                item.setNotaFinal(calcularNotaFinalAlumno(alumno.getId()));
                
                if (alumno.getCurso() != null) {
                    item.setCursoNombre(alumno.getCurso().getNombre());
                }
                
                if (alumno.getEmpresa() != null) {
                    item.setEmpresaNombre(alumno.getEmpresa().getNombre());
                }
                
                ranking.add(item);
            }
        }
        
        // Ordenar por nota descendente
        ranking.sort((a, b) -> b.getNotaFinal().compareTo(a.getNotaFinal()));
        
        // Limitar resultados si se especifica
        if (limite != null && limite > 0 && ranking.size() > limite) {
            return ranking.subList(0, limite);
        }
        
        return ranking;
    }

    // ========================= MÉTODOS PRIVADOS ========================= //

    private BigDecimal calcularNotaFinalAlumno(Long alumnoId) {
        List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumnoId(alumnoId);
        
        if (evaluaciones.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal notaTotal = BigDecimal.ZERO;
        BigDecimal pesoTotal = BigDecimal.ZERO;

        for (Evaluacion eval : evaluaciones) {
            CriterioEvaluacion criterio = eval.getCapacidad().getCriterio();
            BigDecimal peso = criterio.getPeso();
            BigDecimal puntuacionNormalizada = eval.getPuntuacion()
                    .divide(BigDecimal.valueOf(eval.getCapacidad().getPuntuacionMaxima()), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.TEN);
            
            notaTotal = notaTotal.add(puntuacionNormalizada.multiply(peso).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            pesoTotal = pesoTotal.add(peso);
        }

        if (pesoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return notaTotal;
    }
}