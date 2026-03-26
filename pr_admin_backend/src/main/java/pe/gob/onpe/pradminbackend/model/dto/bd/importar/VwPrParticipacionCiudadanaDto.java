package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VwPrParticipacionCiudadanaDto {
	private Integer idFila;
	private String  tipoFiltro;
	private Integer ambitoGeografico;
	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Long    idLocalVotacion;
	private Integer totalElectoresHabiles;
	private Integer totalAsistentes;
	private Integer totalAusentes;
	private Double  porcentajeAsistentes;
	private Double  porcentajeAusentes;
}
