package pe.gob.onpe.consultaopbackend.model.bd.documents;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

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

	@Field("n_porcentaje_votos_validos")
	private Double porcentajeVotosValidos;
	
	@Field("n_porcentaje_votos_emitidos")
	private Double porcentajeVotosEmitidos;
	
	@Field("n_grafico")
	private Integer grafico;
	
	@Field("c_candidato")
	private List<VwPrEleccionBaseDetalleCandidato> candidato;
}
