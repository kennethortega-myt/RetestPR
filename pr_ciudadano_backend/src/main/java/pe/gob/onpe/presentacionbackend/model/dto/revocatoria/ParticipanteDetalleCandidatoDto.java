package pe.gob.onpe.presentacionbackend.model.dto.revocatoria;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipanteDetalleCandidatoDto {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long votos;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer posicionOpcionVoto;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String codigoOpcionVoto;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String descripcionOpcionVoto;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer totalVotos;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Double porcentajeVotosValidos;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Double porcentajeVotosEmitidos;
}
