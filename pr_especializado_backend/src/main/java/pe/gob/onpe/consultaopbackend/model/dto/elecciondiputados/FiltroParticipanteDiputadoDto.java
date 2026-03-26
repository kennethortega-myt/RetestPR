package pe.gob.onpe.consultaopbackend.model.dto.elecciondiputados;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroParticipanteDiputadoDto {
	@NotNull
	private Integer idEleccion;
	@NotEmpty
    private String tipoFiltro;
	private Integer idAmbitoGeografico;
	private String nombreApellidoPartido;
	private Integer idDistritoElectoral;
}
