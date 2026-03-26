package pe.gob.onpe.presentacionbackend.model.bd.documents;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VwPrEleccionBaseDetalleCandidato {
	@Field("c_documento_identidad")
	private String documentoIdentidad;
	@Field("c_apellido_paterno")
	private String apellidoPaterno;
	@Field("c_apellido_materno")
	private String apellidoMaterno;
	@Field("c_nombres")
	private String nombres;
	@Field("c_cargo")
	private String cargo;
	
	@Field(name = "n_candidato")
	private Integer id;
	@Field("n_lista")
	private Integer lista;
	@Field("n_total_votos")
	private Integer votos;
	
	@Field("n_posicion_opcion_voto")
	private Integer posicionOpcionVoto;
	@Field("c_codigo_opcion_voto")
	private String codigoOpcionVoto;
	@Field("c_descripcion_opcion_voto")
	private String descripcionOpcionVoto;
	@Field("n_votos")
	private String totalVotosPorOP;
	@Field("n_porcentaje_votos_validos")
	private Double porcentajeVotosValidos;
	@Field("n_porcentaje_votos_emitidos")
	private Double porcentajeVotosEmitidos;
}
