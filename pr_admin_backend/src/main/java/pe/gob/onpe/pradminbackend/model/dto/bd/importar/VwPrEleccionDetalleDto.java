package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VwPrEleccionDetalleDto {
	@JsonProperty("n_agrupacion_politica")
	private Integer agrupacionPolitica;
	@JsonProperty("c_codigo")
	private String codigo;
	@JsonProperty("c_descripcion")
	private String descripcion;
	@JsonProperty("n_estado")
	private Integer estado;
	@JsonProperty("n_votos")
	private Integer votos;
	@JsonProperty("n_total_votos_validos")
	private Integer totalVotosValidos;
	@JsonProperty("n_porcentaje_votos_validos")
	private Double porcentajeVotosValidos;
	@JsonProperty("n_porcentaje_votos_emitidos")
	private Double porcentajeVotosEmitidos;
	@JsonProperty("n_grafico")
	private Integer grafico;
	@JsonProperty("n_posicion")
	private Integer posicion;
	@JsonProperty("c_cargo")
	private String cargo;
	@JsonProperty("n_cantidad")
	private Integer cantidad;
}
