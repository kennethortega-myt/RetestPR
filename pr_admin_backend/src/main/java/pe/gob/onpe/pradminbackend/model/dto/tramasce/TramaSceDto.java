package pe.gob.onpe.pradminbackend.model.dto.tramasce;

import lombok.*;
import pe.gob.onpe.pradminbackend.model.dto.bd.importar.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TramaSceDto {
	@NotNull(message = "idTransferencia es obligatorio")
	private Long idTransferencia;

	@NotNull(message = "idActa es obligatorio")
	private Long idActa;

	@NotEmpty(message = "vista es obligatorio")
	private String vista;

	@NotEmpty(message = "usuario es obligatorio")
	private String usuario;


	private List<VwPrEleccionDto> tramaEleccion;
	private List<VwPrParticipacionCiudadanaDto> tramaParticipacion;
	private List<VwPrActaDto> tramaActa;
	private List<VwPrEleccionDto> tramaDiputados;
	private List<VwPrEleccionDto> tramaSenadoresDistritoElectoralMultiple;
	private List<VwPrEleccionDto> tramaSenadoresDistritoNacionalUnico;
	private List<VwPrEleccionDto> tramaParlamento;
	private List<VwPrEleccionDto> tramaPresidenciales;
	private List<VwPrEleccionDto> tramaRevocatoriaDistrital;
	private List<VwPrMesaDto> tramaMesa;
}
