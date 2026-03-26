package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrActaDetalle;
import pe.gob.onpe.pradminbackend.model.bd.documents.VwPrActaLineaTiempo;

import java.util.List;

@Getter
@Setter
@Builder
public class ActaMesaResponseDto {
	private Long id;
	private Long idMesa;
	private String codigoMesa;
	private String numeroCopia;
	private Long idUbigeoEleccion;
	private Long idEleccion;
	private Integer idAmbitoGeografico;
	private Long idUbigeo;
	private String ubigeoNivel01;
	private String ubigeoNivel02;
	private String ubigeoNivel03;
	private String centroPoblado;
	private String nombreLocalVotacion;
	private String codigoLocalVotacion;
	private Integer totalElectoresHabiles;
	private Integer totalVotosEmitidos;
	private Integer totalVotosValidos;
	private Integer totalAsistentes;
	private Double  porcentajeParticipacionCiudadana;
	private String  estadoActa;
	private List<VwPrActaDetalle> detalle;
	private List<VwPrActaLineaTiempo> lineaTiempo;
}
