package pe.gob.onpe.pradminbackend.model.dto;

import lombok.Data;

@Data
public class ActaRandomClaimsDto {
	private Long idActa;
	private String numMesa;
	private String ubigeo;
	private String departamento;
	private String provincia;
	private String distrito;
	private String localVotacion;
	private Long idDetUbigeoEleccion;
}
