package pe.gob.onpe.presentacionbackend.model.dto.resumengeneral;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidatoResDto {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer votos;
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
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String cargo;
}
