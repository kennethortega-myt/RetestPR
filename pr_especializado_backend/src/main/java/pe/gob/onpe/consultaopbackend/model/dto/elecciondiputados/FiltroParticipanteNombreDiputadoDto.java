package pe.gob.onpe.consultaopbackend.model.dto.elecciondiputados;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroParticipanteNombreDiputadoDto {
	@NotNull
	private Integer idEleccion;
	@NotEmpty
    private String tipoFiltro;
	private Integer idDistritoElectoral;
	
	private Integer idAgrupacionPolitica;
	private String nombreApellido;
}
