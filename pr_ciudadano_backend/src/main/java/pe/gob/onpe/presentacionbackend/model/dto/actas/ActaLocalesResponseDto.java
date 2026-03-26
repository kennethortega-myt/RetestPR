package pe.gob.onpe.presentacionbackend.model.dto.actas;

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
public class ActaLocalesResponseDto {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String codigoLocalVotacion;
	private String nombreLocalVotacion;
}
