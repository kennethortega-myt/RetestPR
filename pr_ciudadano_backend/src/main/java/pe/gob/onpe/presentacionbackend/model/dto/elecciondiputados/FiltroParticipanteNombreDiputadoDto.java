package pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
public class FiltroParticipanteNombreDiputadoDto {
	@NotNull
	private Integer idEleccion;
	@NotEmpty
    private String tipoFiltro;
	private Integer idDistritoElectoral;
	
	private Integer idAgrupacionPolitica;
	private String nombreApellido;
}
