package com.gestionpracticas.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class EstadisticasEmpleabilidadDTO {
    private Integer totalAlumnosFinalizados;
    private Integer alumnosContratados;
    private BigDecimal porcentajeContratacion;
    
    // Empresas que más contratan (ordenadas descendentemente)
    private List<EstadisticasItemDTO> empresasQueMasContratan;
    
    // Evolución temporal de contrataciones
    private List<EvolucionContratacionDTO> evolucionTemporal;
    
    // Correlación entre notas y contratación (-1 a 1)
    private BigDecimal correlacionNotasContratacion;
}