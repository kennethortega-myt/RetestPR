package pe.gob.onpe.pradminbackend.model.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
public class ImportarRequest {
	private String acronimoProceso;
	private Integer getBd;
	private Integer getVistaEleccion;
	private Integer getVistaResumen;
	private Integer getVistaActa;
	private Integer getVistaParticipacionCiudadana;
	private Integer getVistaMesa;
	private Integer getVistaTotalCandidatosPorAgrupacionPolitica;
	private Integer getMenu;
}
