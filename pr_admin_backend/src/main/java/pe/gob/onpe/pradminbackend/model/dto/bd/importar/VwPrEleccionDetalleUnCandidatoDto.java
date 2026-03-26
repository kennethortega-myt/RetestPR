package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VwPrEleccionDetalleUnCandidatoDto extends VwPrEleccionDetalleDto {
	@JsonProperty("c_candidato")
	private List<VwPrEleccionDetalleUnCandidatoListDto> candidato;
}
