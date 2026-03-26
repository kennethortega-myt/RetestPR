package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FiltroParticipacionReporteDto {

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
