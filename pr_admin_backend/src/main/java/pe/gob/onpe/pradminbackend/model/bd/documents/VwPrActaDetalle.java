package pe.gob.onpe.pradminbackend.model.bd.documents;

import java.util.List;

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
public class VwPrActaDetalle {
	
	@Field("n_agrupacion_politica")
	private Integer adAgrupacionPolitica;
	
	@Field("n_posicion")
	private Integer adPosicion;
	
	@Field("c_codigo")
	private String adCodigo;
	
	@Field("c_descripcion")
	private String adDescripcion;
	
	@Field("n_estado")
	private Integer adEstado;

	@Field("n_votos")
	private Integer adVotos;
	
	@Field("n_total_votos_validos")
	private Integer adTotalVotosValidos;
	
	@Field("c_cargo")
	private String adCargo;

	@Field("n_porcentaje_votos_validos")
	private Double adPorcentajeVotosValidos;
	
	@Field("n_porcentaje_votos_emitidos")
	private Double adPorcentajeVotosEmitidos;
	
	@Field("n_grafico")
	private Integer adGrafico;
	
	@Field("c_candidato")
	private List<VwPrActaDetalleCandidato> candidato;
}
