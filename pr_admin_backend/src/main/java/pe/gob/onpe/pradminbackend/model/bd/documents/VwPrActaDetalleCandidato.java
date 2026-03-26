package pe.gob.onpe.pradminbackend.model.bd.documents;

import org.springframework.data.mongodb.core.mapping.Field;

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
public class VwPrActaDetalleCandidato {

	@Field(name = "n_candidato")
	private Integer id;
	
	@Field("n_lista")
	private Integer lista;
	
	@Field("n_total_votos")
	private Long votos;
	
	//Revocatoria
	@Field("n_posicion_opcion_voto")
	private Integer posicionOpcionVoto;
	@Field("c_codigo_opcion_voto")
	private String codigoOpcionVoto;
	@Field("c_descripcion_opcion_voto")
	private String descripcionOpcionVoto;
	@Field("n_posicion")
	private Integer posicion;
	@Field("n_porcentaje_votos_validos")
	private Double porcentajeVotosValidos;
	@Field("n_porcentaje_votos_emitidos")
	private Double porcentajeVotosEmitidos;
}
