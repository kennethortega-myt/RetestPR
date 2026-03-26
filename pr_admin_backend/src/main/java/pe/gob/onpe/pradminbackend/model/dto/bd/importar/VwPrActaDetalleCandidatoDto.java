package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VwPrActaDetalleCandidatoDto {
	@JsonProperty("n_candidato")
	private Integer migraId;
	@JsonProperty("n_lista")
	private Integer migraLista;
	@JsonProperty("n_total_votos")
	private Long migraVotos;
	
	//Revocatoria
	@JsonProperty("n_votos")
	private Integer migraVotosRevo;
	@JsonProperty("n_posicion_opcion_voto")
	private Integer migraPosicionOpcionVoto;
	@JsonProperty("c_codigo_opcion_voto")
	private String migraCodigoOpcionVoto;
	@JsonProperty("c_descripcion_opcion_voto")
	private String migraDescripcionOpcionVoto;
	@JsonProperty("n_posicion")
	private Integer migraPosicion;
	@JsonProperty("n_porcentaje_votos_validos")
	private Double migraPorcentajeVotosValidos;
	@JsonProperty("n_porcentaje_votos_emitidos")
	private Double migraPorcentajeVotosEmitidos;
}
