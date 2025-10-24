package com.gestionpracticas.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriterioEvaluacionSearchDTO {
	private long id;
	private String nombre;
	private String descripcion;
	private BigDecimal peso;
	
	//Metadata
	private Boolean activo;
}
