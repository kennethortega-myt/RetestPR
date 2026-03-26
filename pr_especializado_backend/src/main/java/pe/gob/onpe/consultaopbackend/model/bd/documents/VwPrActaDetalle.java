package pe.gob.onpe.consultaopbackend.model.bd.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VwPrActaDetalle {
	@Field("n_agrupacion_politica")
	private Integer nAgrupacionPolitica;
	
	@Field("n_posicion")
	private Integer nPosicion;
	
	@Field("c_codigo")
	private String cCodigo;
	
	@Field("c_descripcion")
	private String descripcion;
	
	@Field("n_estado")
	private Integer estado;

	@Field("n_votos")
	private Integer nVotos;

	@Field("n_porcentaje_votos_validos")
	private Double nPorcentajeVotosValidos;
	
	@Field("n_porcentaje_votos_emitidos")
	private Double nPorcentajeVotosEmitidos;
	
	@Field("n_grafico")
	private Integer grafico;
	
	@Field("c_candidato")
	private List<VwPrActaDetalleCandidato> candidato;
}
