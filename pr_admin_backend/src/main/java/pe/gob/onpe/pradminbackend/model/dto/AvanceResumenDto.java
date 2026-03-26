package pe.gob.onpe.pradminbackend.model.dto;

import lombok.Data;

@Data
public class AvanceResumenDto {

	private AvanceResumenVotoDto validados;
	private AvanceResumenVotoDto blancos;
	private AvanceResumenVotoDto nulos;
	private AvanceResumenVotoDto emitidos;
	
}
