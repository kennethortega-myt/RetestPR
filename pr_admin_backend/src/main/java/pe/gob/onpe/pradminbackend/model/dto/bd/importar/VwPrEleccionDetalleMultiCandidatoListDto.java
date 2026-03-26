package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VwPrEleccionDetalleMultiCandidatoListDto {
	@JsonProperty("n_candidato")
	private Integer id;
	@JsonProperty("n_lista")
	private Integer lista;
	@JsonProperty("n_total_votos")
	private Integer totalVotos;
	
	@JsonProperty("n_posicion_opcion_voto")
	private Integer posicionOpcionVoto;
	@JsonProperty("c_codigo_opcion_voto")
	private String codigoOpcionVoto;
	@JsonProperty("c_descripcion_opcion_voto")
	private String descripcionOpcionVoto;
	@JsonProperty("n_votos")
	private Integer votos;
	@JsonProperty("n_porcentaje_votos_validos")
	private Double porcentajeVotosValidos;
	@JsonProperty("n_porcentaje_votos_emitidos")
	private Double porcentajeVotosEmitidos;
}
