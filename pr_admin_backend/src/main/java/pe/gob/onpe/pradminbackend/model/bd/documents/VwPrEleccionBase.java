package pe.gob.onpe.pradminbackend.model.bd.documents;

import java.util.List;

import org.springframework.data.annotation.Id;
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
public abstract class VwPrEleccionBase extends Auditoria {
	
	@Id
	@Field("id")
	private Integer id;

	@Field("n_tipo_eleccion")
	private Integer tipoEleccion;

	@Field("c_tipo_filtro")
	private String tipoFiltro;
	
	@Field("n_det_ubigeo_eleccion")
	private Long idDetUbigeoEleccion;

	@Field("n_ambito_geografico")
	private Integer ambitoGeografico;
	
	@Field("n_distrito_electoral")
	private Integer distritoElectoral;

	@Field("n_ubigeo_nivel_01")
	private Integer ubigeoNivel01;

	@Field("n_ubigeo_nivel_02")
	private Integer ubigeoNivel02;

	@Field("n_ubigeo_nivel_03")
	private Integer ubigeoNivel03;

	@Field("n_total_electores_habiles")
	private Integer totalElectoresHabiles;
	
	@Field("n_total_actas")
	private Integer totalActas;
	
	@Field("n_total_votos_emitidos")
	private Integer totalVotosEmitidos;
	
	@Field("n_total_votos_validos")
	private Integer totalVotosValidos;

	@Field("n_participacion_ciudadana")
	private Integer participacionCiudadana;

	@Field("n_porcentaje_participacion_ciudadana")
	private Double porcentajeParticipacionCiudadana;

	@Field("n_actas_contabilizadas")
	private Integer actasContabilizadas;

	@Field("n_porcentaje_actas_contabilizadas")
	private Double porcentajeActasContabilizadas;

	@Field("n_actas_observadas_enviadas")
	private Integer actasObservadasEnviadas;

	@Field("n_porcentaje_actas_observadas_enviadas")
	private Double porcentajeActasObservadasEnviadas;

	@Field("n_actas_pendientes")
	private Integer actasPendientes;

	@Field("n_porcentaje_actas_pendientes")
	private Double porcentajeActasPendientes;
	
	@Field("c_detalle")
	private List<VwPrEleccionBaseDetalle> detalle;
}
