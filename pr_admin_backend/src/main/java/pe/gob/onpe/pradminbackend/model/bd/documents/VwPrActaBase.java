package pe.gob.onpe.pradminbackend.model.bd.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VwPrActaBase extends Auditoria {
	
	@Field(name = "n_mesa")
	private Long idMesa;
	
	@Field(name = "c_codigo_mesa")
	private String codigoMesa;
	
	@Field(name = "n_solucion_tecnnologica")
	private Long idSolucionTecnologica;
	
	@Field(name = "c_descripcion_solucion_tecnnologica")
	private String descripcionSolucionTecnologica;
	
	@Field(name = "c_numero_copia")
	private String numeroCopia;
	
	@Field(name = "c_digito_chequeo_escrutinio")
	private String digitoChequeoEscrutinio;
	
	@Field(name = "c_digito_chequeo_instalacion")
	private String digitoChequeoInstalacion;

	@Field(name = "c_digito_chequeo_sufragio")
	private String digitoChequeoSufragio;
	
	@Field(name = "n_ubigeo_eleccion")
	private Long idUbigeoEleccion;
	
	@Field(name = "n_eleccion")
	private Long idEleccion;
	
	@Field("n_orden")
	private Integer orden;
	
	@Field(name = "n_ambito_geografico")
	private Integer idAmbitoGeografico;
	
	@Field(name = "n_ubigeo_nivel_01")
	private Integer nubigeoNivel01;

	@Field(name = "n_ubigeo_nivel_02")
	private Integer nubigeoNivel02;
	
	@Field(name = "n_ubigeo")
	private Long idUbigeo;
	
	@Field(name = "n_distrito_electoral")
	private Integer idDistritoElectoral;
	
	@Field(name = "c_ubigeo_nivel_01")
	private String ubigeoNombreNivel01;
	
	@Field(name = "c_ubigeo_nivel_02")
	private String ubigeoNombreNivel02;
	
	@Field(name = "c_ubigeo_nivel_03")
	private String ubigeoNombreNivel03;
	
	@Field(name = "c_centro_poblado")
	private String centroPoblado;
	
	@Field(name = "n_local_votacion")
	private Long idLocalVotacion;
	
	@Field(name = "c_nombre_local_votacion")
	private String nombreLocalVotacion;
	
	@Field(name = "c_codigo_local_votacion")
	private String codigoLocalVotacion;
	
	@Field(name = "n_total_electores_habiles")
	private Integer totalElectoresHabiles;
	
	@Field(name = "n_total_votos_emitidos")
	private Integer totalVotosEmitidos;
	
	@Field(name = "n_total_votos_validos")
	private Integer totalVotosValidos;
	
	@Field(name = "n_total_asistentes")
	private Integer totalAsistentes;
	
	@Field(name = "n_porcentaje_participacion_ciudadana")
	private Double  porcentajeParticipacionCiudadana;
	
	@Field(name = "c_estado_acta")
	private String  estadoActa;
	
	@Field(name = "c_estado_computo")
	private String estadoComputo;
	
	@Field(name = "c_codigo_estado_acta")
	private String  codigoEstadoActa;
	
	@Field(name = "c_descripcion_estado_acta")
	private String descripcionEstadoActa;

	@Field(name = "c_estado_acta_resolucion")
	private String estadoActaResolucion;

	@Field(name = "c_descripcion_estado_acta_resolucion")
	private String estadoDescripcionActaResolucion;
	
	@Field(name = "c_descripcion_sub_estado_acta")
	private String descripcionSubEstadoActa;
	
	@Field(name = "c_detalle")
	private List<VwPrActaDetalle> detalle;
	
	@Field(name = "c_linea_tiempo")
	private List<VwPrActaLineaTiempo> lineaTiempo;

}
