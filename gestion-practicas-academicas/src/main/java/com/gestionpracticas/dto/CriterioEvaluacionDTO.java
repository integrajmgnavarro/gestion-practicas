package com.gestionpracticas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriterioEvaluacionDTO {
	private long id;
	private String nombre;
	private String descripcion;
	private BigDecimal peso;
	
	//Metadata
	private Boolean activo;
}
