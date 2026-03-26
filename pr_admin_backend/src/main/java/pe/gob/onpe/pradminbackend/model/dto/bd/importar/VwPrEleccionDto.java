package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class VwPrEleccionDto {
	private Integer idFila;
	private Integer tipoEleccion;
	private Integer distritoElectoral;
	private Long idDetUbigeoEleccion;
	private String  tipoFiltro;
	private Integer ambitoGeografico;
	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Integer totalElectoresHabiles;
	private Integer participacionCiudadana;
	private Double  porcentParticipacionCiudadana;
	private Integer totalActas;
	private Integer actasContabilizadas;
	private Double  porcentajeActasContabilizadas;
	private Integer actasObservadasEnviadas;
	private Double  porcentajeActasObservadasEnviadas;
	private Integer actasPendientes;
	private Double  porcentajeActasPendientes;
	private Integer totalVotosEmitidos;
	private Integer totalVotosValidos;
	private String  detalle;
}
