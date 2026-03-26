package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.pradminbackend.model.dto.resumengeneral.ParticipanteDto;

@Getter
@Setter
@Builder
public class ActaMapaCalorResponseDto {

	private Integer ambitoGeografico;
	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Integer distritoElectoral;
	private Double porcentajeActasContabilizadas;
	private Integer actasContabilizadas;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ParticipanteDto participante;
}
