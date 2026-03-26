package pe.gob.onpe.presentacionbackend.model.dto.actas;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.presentacionbackend.model.dto.resumengeneral.ParticipanteDto;

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

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Double porcentajeAsistentes;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer asistentes;
}
