package pe.gob.onpe.presentacionbackend.model.bd.documents;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VwPrEleccionBaseDetalle {

	@Field("n_agrupacion_politica")
	private Integer agrupacionPolitica;
	
	@Field("n_posicion")
	private Integer posicion;
	
	@Field("c_codigo")
	private String codigo;
	
	@Field("c_descripcion")
	private String descripcion;
	
	@Field("n_estado")
	private Integer estado;

	@Field("n_votos")
	private Integer votos;
	
	@Field("n_total_votos_validos")
	private Integer totalVotosValidos;

	@Field("n_porcentaje_votos_validos")
	private Double porcentajeVotosValidos;
	
	@Field("n_porcentaje_votos_emitidos")
	private Double porcentajeVotosEmitidos;
	
	@Field("n_grafico")
	private Integer grafico;
	
	@Field("c_cargo")
	private String cargo;

	@Field("n_cantidad")
	private Integer cantidad;
	
	@Field("c_candidato")
	private List<VwPrEleccionBaseDetalleCandidato> candidato;
}
