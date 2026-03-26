package pe.gob.onpe.presentacionbackend.model.dto.actas;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ActaResponseDetalleCandidatoDto {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String cDocumentoIdentidad;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String apellidoPaterno;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String apellidoMaterno;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String nombres;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String cargo;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long votos;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer posicionOpcionVoto;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String codigoOpcionVoto;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String descripcionOpcionVoto;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Double porcentajeVotosValidos;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Double porcentajeVotosEmitidos;
}
