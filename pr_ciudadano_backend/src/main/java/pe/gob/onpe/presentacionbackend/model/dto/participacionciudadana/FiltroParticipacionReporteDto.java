package pe.gob.onpe.presentacionbackend.model.dto.participacionciudadana;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Setter
@Getter
public class FiltroParticipacionReporteDto {
	
	private Integer tipoReporte;

	@NotNull(message = "tipoFiltro es obligatorio")
	@NotBlank
	private String tipoFiltro;
	private Integer idAmbitoGeografico;
	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Long idLocalVotacion;

	private String nombreProceso;
	private String nombreEleccion;

}
