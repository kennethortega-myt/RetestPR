package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VwPrActaLineaTiempoDto {
	@JsonProperty("c_codigo_estado_acta")
	private String codigoEstadoActa;
	@JsonProperty("c_descripcion_estado_acta")
	private String descripcionEstadoActa;
	@JsonProperty("c_descripcion_estado_acta_resolucion")
	private String descripcionEstadoActaResolucion;
	@JsonProperty("c_fecha_registro")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Lima")
	private Date fechaRegistro;
}
