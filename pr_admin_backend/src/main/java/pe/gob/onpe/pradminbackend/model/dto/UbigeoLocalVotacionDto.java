package pe.gob.onpe.pradminbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbigeoLocalVotacionDto {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long codigoLocalVotacion;
	private String nombreLocalVotacion;
}
