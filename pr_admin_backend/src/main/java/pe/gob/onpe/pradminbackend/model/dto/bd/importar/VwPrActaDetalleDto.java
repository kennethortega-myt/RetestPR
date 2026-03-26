package pe.gob.onpe.pradminbackend.model.dto.bd.importar;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VwPrActaDetalleDto {
	@JsonProperty("n_agrupacion_politica")
	private Integer migraAgrupacionPolitica;
	@JsonProperty("c_codigo")
	private String migraCodigo;
	@JsonProperty("c_descripcion")
	private String migraDescripcion;
	@JsonProperty("n_estado")
	private Integer migraEstado;
	@JsonProperty("n_votos")
	private Integer migraVotos;
	@JsonProperty("n_total_votos_validos")
	private Integer migraTotalVotosValidos;
	@JsonProperty("c_cargo")
	private String migraCargo;
	@JsonProperty("n_porcentaje_votos_validos")
	private Double migraPorcentajeVotosValidos;
	@JsonProperty("n_porcentaje_votos_emitidos")
	private Double migraPorcentajeVotosEmitidos;
	@JsonProperty("n_grafico")
	private Integer migraGrafico;
	@JsonProperty("n_posicion")
	private Integer migraPosicion;
	@JsonProperty("c_candidato")
	private List<VwPrActaDetalleCandidatoDto> migraCandidato;
}
