package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
