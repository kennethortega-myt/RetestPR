package pe.gob.onpe.consultaopbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
