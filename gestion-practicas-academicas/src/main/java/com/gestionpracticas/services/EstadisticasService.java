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
    private final CursoRepository cursoRepository;
    private final EmpresaRepository empresaRepository;
    private final TutorPracticasRepository tutorPracticasRepository;

    // ========================= ESTADÍSTICAS GENERALES ========================= //

    @Transactional(readOnly = true)
    public EstadisticasGeneralesDTO getEstadisticasGenerales() {
        EstadisticasGeneralesDTO stats = new EstadisticasGeneralesDTO();
        
        // Tasa de aprobados por curso
        stats.setTasaAprobadosPorCurso(calcularTasaAprobadosPorCurso());
        
        // Notas medias por empresa
        stats.setNotasMediasPorEmpresa(calcularNotasMediasPorEmpresa());
        
        // Notas medias por tutor
        stats.setNotasMediasPorTutor(calcularNotasMediasPorTutor());
        
        // Distribución de calificaciones
        stats.setDistribucionCalificaciones(calcularDistribucionCalificaciones());
        
        // Tiempo medio de prácticas
        EstadisticasDuracion duracion = calcularEstadisticasDuracion();
        stats.setDuracionMediaDias(duracion.getMedia());
        stats.setDuracionMinimaDias(duracion.getMinima());
        stats.setDuracionMaximaDias(duracion.getMaxima());
        
        return stats;
    }

    // ========================= ESTADÍSTICAS DE EMPLEABILIDAD ========================= //

    @Transactional(readOnly = true)
    public EstadisticasEmpleabilidadDTO getEstadisticasEmpleabilidad() {
        EstadisticasEmpleabilidadDTO stats = new EstadisticasEmpleabilidadDTO();
        
        // Alumnos finalizados (con fecha fin en el pasado)
        List<Alumno> alumnosFinalizados = alumnoRepository.findByFechaFinIsNotNull().stream()
                .filter(a -> a.getFechaFin().isBefore(LocalDate.now()) || a.getFechaFin().isEqual(LocalDate.now()))
                .collect(Collectors.toList());
        
        stats.setTotalAlumnosFinalizados(alumnosFinalizados.size());
        
        // Alumnos contratados
        long contratados = alumnosFinalizados.stream()
                .filter(a -> a.getContratado() != null && a.getContratado())
                .count();
        
        stats.setAlumnosContratados((int) contratados);
        
        // Porcentaje de contratación
        if (alumnosFinalizados.size() > 0) {
            BigDecimal porcentaje = BigDecimal.valueOf(contratados)
                    .divide(BigDecimal.valueOf(alumnosFinalizados.size()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            stats.setPorcentajeContratacion(porcentaje);
        } else {
            stats.setPorcentajeContratacion(BigDecimal.ZERO);
        }
        
        // Empresas que más contratan
        stats.setEmpresasQueMasContratan(calcularEmpresasQueMasContratan(alumnosFinalizados));
        
        // Evolución temporal de contrataciones
        stats.setEvolucionTemporal(calcularEvolucionContrataciones(alumnosFinalizados));
        
        // Correlación notas-contratación
        stats.setCorrelacionNotasContratacion(calcularCorrelacionNotasContratacion(alumnosFinalizados));
        
        return stats;
    }

    // ========================= MÉTODOS PRIVADOS - ESTADÍSTICAS GENERALES ========================= //

    private List<EstadisticasItemDTO> calcularTasaAprobadosPorCurso() {
        List<EstadisticasItemDTO> estadisticas = new ArrayList<>();
        List<Curso> cursos = cursoRepository.findAll();
        
        for (Curso curso : cursos) {
            List<Alumno> alumnos = alumnoRepository.findByCurso_Id(curso.getId());
            
            EstadisticasItemDTO item = new EstadisticasItemDTO();
            item.setId(curso.getId());
            item.setNombre(curso.getNombre());
            item.setTotalAlumnos(alumnos.size());
            
            int evaluados = 0;
            int aprobados = 0;
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
                }
            }
            
            item.setAprobados(aprobados);
            item.setSuspendidos(evaluados - aprobados);
            
            if (evaluados > 0) {
                item.setNotaMedia(sumaNotas.divide(BigDecimal.valueOf(evaluados), 2, RoundingMode.HALF_UP));
                BigDecimal tasa = BigDecimal.valueOf(aprobados)
                        .divide(BigDecimal.valueOf(evaluados), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
                item.setTasaAprobados(tasa);
            } else {
                item.setNotaMedia(BigDecimal.ZERO);
                item.setTasaAprobados(BigDecimal.ZERO);
            }
            
            estadisticas.add(item);
        }
        
        return estadisticas;
    }

    private List<EstadisticasItemDTO> calcularNotasMediasPorEmpresa() {
        List<EstadisticasItemDTO> estadisticas = new ArrayList<>();
        List<Empresa> empresas = empresaRepository.findAll();
        
        for (Empresa empresa : empresas) {
            List<Alumno> alumnos = alumnoRepository.findByEmpresa_Id(empresa.getId());
            
            EstadisticasItemDTO item = new EstadisticasItemDTO();
            item.setId(empresa.getId());
            item.setNombre(empresa.getNombre());
            item.setTotalAlumnos(alumnos.size());
            
            int evaluados = 0;
            BigDecimal sumaNotas = BigDecimal.ZERO;
            
            for (Alumno alumno : alumnos) {
                List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumno_Id(alumno.getId());
                if (!evaluaciones.isEmpty()) {
                    evaluados++;
                    BigDecimal nota = calcularNotaFinalAlumno(alumno.getId());
                    sumaNotas = sumaNotas.add(nota);
                }
            }
            
            if (evaluados > 0) {
                item.setNotaMedia(sumaNotas.divide(BigDecimal.valueOf(evaluados), 2, RoundingMode.HALF_UP));
            } else {
                item.setNotaMedia(BigDecimal.ZERO);
            }
            
            estadisticas.add(item);
        }
        
        // Ordenar por nota media descendente
        estadisticas.sort((a, b) -> b.getNotaMedia().compareTo(a.getNotaMedia()));
        
        return estadisticas;
    }

    private List<EstadisticasItemDTO> calcularNotasMediasPorTutor() {
        List<EstadisticasItemDTO> estadisticas = new ArrayList<>();
        List<TutorPracticas> tutores = tutorPracticasRepository.findAll();
        
        for (TutorPracticas tutor : tutores) {
            List<Alumno> alumnos = alumnoRepository.findByTutorPracticas_Id(tutor.getId());
            
            EstadisticasItemDTO item = new EstadisticasItemDTO();
            item.setId(tutor.getId());
            item.setNombre(tutor.getNombre() + " " + tutor.getApellidos());
            item.setTotalAlumnos(alumnos.size());
            
            int evaluados = 0;
            BigDecimal sumaNotas = BigDecimal.ZERO;
            
            for (Alumno alumno : alumnos) {
                List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumno_Id(alumno.getId());
                if (!evaluaciones.isEmpty()) {
                    evaluados++;
                    BigDecimal nota = calcularNotaFinalAlumno(alumno.getId());
                    sumaNotas = sumaNotas.add(nota);
                }
            }
            
            if (evaluados > 0) {
                item.setNotaMedia(sumaNotas.divide(BigDecimal.valueOf(evaluados), 2, RoundingMode.HALF_UP));
            } else {
                item.setNotaMedia(BigDecimal.ZERO);
            }
            
            estadisticas.add(item);
        }
        
        // Ordenar por nota media descendente
        estadisticas.sort((a, b) -> b.getNotaMedia().compareTo(a.getNotaMedia()));
        
        return estadisticas;
    }

    private Map<String, Integer> calcularDistribucionCalificaciones() {
        Map<String, Integer> distribucion = new LinkedHashMap<>();
        distribucion.put("Sobresaliente (9-10)", 0);
        distribucion.put("Notable (7-8.99)", 0);
        distribucion.put("Bien (6-6.99)", 0);
        distribucion.put("Suficiente (5-5.99)", 0);
        distribucion.put("Insuficiente (<5)", 0);
        
        List<Alumno> alumnos = alumnoRepository.findAll();
        
        for (Alumno alumno : alumnos) {
            List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumno_Id(alumno.getId());
            if (!evaluaciones.isEmpty()) {
                BigDecimal nota = calcularNotaFinalAlumno(alumno.getId());
                
                if (nota.compareTo(BigDecimal.valueOf(9.0)) >= 0) {
                    distribucion.put("Sobresaliente (9-10)", distribucion.get("Sobresaliente (9-10)") + 1);
                } else if (nota.compareTo(BigDecimal.valueOf(7.0)) >= 0) {
                    distribucion.put("Notable (7-8.99)", distribucion.get("Notable (7-8.99)") + 1);
                } else if (nota.compareTo(BigDecimal.valueOf(6.0)) >= 0) {
                    distribucion.put("Bien (6-6.99)", distribucion.get("Bien (6-6.99)") + 1);
                } else if (nota.compareTo(BigDecimal.valueOf(5.0)) >= 0) {
                    distribucion.put("Suficiente (5-5.99)", distribucion.get("Suficiente (5-5.99)") + 1);
                } else {
                    distribucion.put("Insuficiente (<5)", distribucion.get("Insuficiente (<5)") + 1);
                }
            }
        }
        
        return distribucion;
    }

    private EstadisticasDuracion calcularEstadisticasDuracion() {
        List<Alumno> alumnos = alumnoRepository.findByDuracionPracticasIsNotNull();
        
        if (alumnos.isEmpty()) {
            return new EstadisticasDuracion(0, 0, 0);
        }
        
        List<Integer> duraciones = alumnos.stream()
                .map(Alumno::getDuracionPracticas)
                .collect(Collectors.toList());
        
        int suma = duraciones.stream().mapToInt(Integer::intValue).sum();
        int media = suma / duraciones.size();
        int minima = Collections.min(duraciones);
        int maxima = Collections.max(duraciones);
        
        return new EstadisticasDuracion(media, minima, maxima);
    }

    // ========================= MÉTODOS PRIVADOS - EMPLEABILIDAD ========================= //

    private List<EstadisticasItemDTO> calcularEmpresasQueMasContratan(List<Alumno> alumnosFinalizados) {
        // Agrupar por empresa y contar contratados
        Map<Long, EmpresaContratacion> empresasMap = new HashMap<>();
        
        for (Alumno alumno : alumnosFinalizados) {
            if (alumno.getEmpresa() != null) {
                Long empresaId = alumno.getEmpresa().getId();
                
                empresasMap.putIfAbsent(empresaId, 
                    new EmpresaContratacion(
                        empresaId, 
                        alumno.getEmpresa().getNombre(), 
                        0, 
                        0
                    )
                );
                
                EmpresaContratacion ec = empresasMap.get(empresaId);
                ec.totalAlumnos++;
                
                if (alumno.getContratado() != null && alumno.getContratado()) {
                    ec.contratados++;
                }
            }
        }
        
        // Convertir a EstadisticasItemDTO
        List<EstadisticasItemDTO> resultado = empresasMap.values().stream()
                .map(ec -> {
                    EstadisticasItemDTO item = new EstadisticasItemDTO();
                    item.setId(ec.empresaId);
                    item.setNombre(ec.empresaNombre);
                    item.setTotalAlumnos(ec.totalAlumnos);
                    item.setContratados(ec.contratados);
                    return item;
                })
                .sorted((a, b) -> b.getContratados().compareTo(a.getContratados()))
                .collect(Collectors.toList());
        
        return resultado;
    }

    private List<EvolucionContratacionDTO> calcularEvolucionContrataciones(List<Alumno> alumnosFinalizados) {
        // Agrupar contrataciones por año y mes de fecha fin
        Map<String, Integer> evolucionMap = new TreeMap<>();
        
        for (Alumno alumno : alumnosFinalizados) {
            if (alumno.getContratado() != null && alumno.getContratado() && alumno.getFechaFin() != null) {
                String periodo = alumno.getFechaFin().getYear() + "-" + 
                                String.format("%02d", alumno.getFechaFin().getMonthValue());
                evolucionMap.put(periodo, evolucionMap.getOrDefault(periodo, 0) + 1);
            }
        }
        
        // Convertir a DTO
        List<EvolucionContratacionDTO> resultado = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : evolucionMap.entrySet()) {
            String[] partes = entry.getKey().split("-");
            EvolucionContratacionDTO dto = new EvolucionContratacionDTO();
            dto.setAnio(Integer.parseInt(partes[0]));
            dto.setMes(Integer.parseInt(partes[1]));
            dto.setContrataciones(entry.getValue());
            dto.setPeriodo(entry.getKey());
            resultado.add(dto);
        }
        
        return resultado;
    }

    private BigDecimal calcularCorrelacionNotasContratacion(List<Alumno> alumnosFinalizados) {
        List<BigDecimal> notas = new ArrayList<>();
        List<Integer> contratados = new ArrayList<>();
        
        for (Alumno alumno : alumnosFinalizados) {
            List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumno_Id(alumno.getId());
            if (!evaluaciones.isEmpty()) {
                BigDecimal nota = calcularNotaFinalAlumno(alumno.getId());
                notas.add(nota);
                contratados.add((alumno.getContratado() != null && alumno.getContratado()) ? 1 : 0);
            }
        }
        
        if (notas.size() < 2) {
            return BigDecimal.ZERO;
        }
        
        // Calcular coeficiente de correlación de Pearson
        double mediaNotas = notas.stream()
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);
        
        double mediaContratados = contratados.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        
        double numerador = 0.0;
        double denominador1 = 0.0;
        double denominador2 = 0.0;
        
        for (int i = 0; i < notas.size(); i++) {
            double difNota = notas.get(i).doubleValue() - mediaNotas;
            double difContratado = contratados.get(i) - mediaContratados;
            
            numerador += difNota * difContratado;
            denominador1 += difNota * difNota;
            denominador2 += difContratado * difContratado;
        }
        
        double denominador = Math.sqrt(denominador1 * denominador2);
        
        if (denominador == 0.0) {
            return BigDecimal.ZERO;
        }
        
        double correlacion = numerador / denominador;
        return BigDecimal.valueOf(correlacion).setScale(4, RoundingMode.HALF_UP);
    }

    // ========================= MÉTODO AUXILIAR PARA CALCULAR NOTA FINAL ========================= //

    private BigDecimal calcularNotaFinalAlumno(Long alumnoId) {
        List<Evaluacion> evaluaciones = evaluacionRepository.findByAlumno_Id(alumnoId);
        
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

    // ========================= CLASES AUXILIARES INTERNAS ========================= //

    private static class EstadisticasDuracion {
        private final Integer media;
        private final Integer minima;
        private final Integer maxima;

        public EstadisticasDuracion(Integer media, Integer minima, Integer maxima) {
            this.media = media;
            this.minima = minima;
            this.maxima = maxima;
        }

        public Integer getMedia() { return media; }
        public Integer getMinima() { return minima; }
        public Integer getMaxima() { return maxima; }
    }

    private static class EmpresaContratacion {
        private final Long empresaId;
        private final String empresaNombre;
        private int totalAlumnos;
        private int contratados;

        public EmpresaContratacion(Long empresaId, String empresaNombre, int totalAlumnos, int contratados) {
            this.empresaId = empresaId;
            this.empresaNombre = empresaNombre;
            this.totalAlumnos = totalAlumnos;
            this.contratados = contratados;
        }
    }
}