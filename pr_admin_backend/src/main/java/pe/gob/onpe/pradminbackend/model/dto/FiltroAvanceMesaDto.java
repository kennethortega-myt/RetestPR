package pe.gob.onpe.pradminbackend.model.dto;

import lombok.Data;

@Data
public class FiltroAvanceMesaDto {

	private Long   idProceso;
	private Long   idAmbito;
	private Long   idCentroComputo;
	private String departamento;
	private String provincia;
	private Long   idUbigeo;
	private String mesa;
	
}
