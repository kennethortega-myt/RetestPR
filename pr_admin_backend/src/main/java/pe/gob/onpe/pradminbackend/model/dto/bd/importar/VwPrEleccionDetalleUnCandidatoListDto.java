package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VwPrEleccionDetalleUnCandidatoListDto {
	@JsonProperty("c_documento_identidad")
	private String documentoIdentidad;
	@JsonProperty("c_apellido_paterno")
	private String apellidoPaterno;
	@JsonProperty("c_apellido_materno")
	private String apellidoMaterno;
	@JsonProperty("c_nombres")
	private String nombres;
	@JsonProperty("cargo")
	private String cargo;
}
