package pe.gob.onpe.consultaopbackend.model.dto.senadoresdistritomultiple;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestParticipanteSenadorDistritoMultipleDto {
	@NotNull
	@Min(1)
	private Integer idEleccion;
	
	@NotEmpty
	private String tipoFiltro;
	
	@Min(1)
	private Integer idDistritoElectoral;
}
