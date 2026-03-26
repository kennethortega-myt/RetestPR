package pe.gob.onpe.presentacionbackend.model.dto.candidatos;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiltroCandidatosDiputadosDto {

	@NotNull
	private Integer idEleccion;
	@NotNull
	private Integer idDistritoElectoral;
	@NotNull
	private Integer idAgrupacionPolitica;
	
	private String nombreApellido;
}
