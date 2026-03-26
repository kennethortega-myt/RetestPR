package pe.gob.onpe.consultaopbackend.model.dto.actas;

import lombok.*;
import pe.gob.onpe.consultaopbackend.model.bd.documents.VwPrActaLineaTiempo;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActaResponseDto {
	private Long id;
	private Long idMesa;
	private Integer idAmbitoGeografico;
	private String codigoMesa;
	private String descripcionMesa;
	private Long idEleccion;
	private String ubigeoNivel01;
	private String ubigeoNivel02;
	private String ubigeoNivel03;
	private String centroPoblado;
	private String nombreLocalVotacion;
	private Integer totalElectoresHabiles;
	private Integer totalVotosEmitidos;
	private Integer totalVotosValidos;
	private Integer totalAsistentes;
	private Double porcentajeParticipacionCiudadana;
	private String estadoActa;	
	private String estadoComputo;
	private String codigoEstadoActa;
	private String descripcionEstadoActa;
	private String estadoDescripcionActaResolucion;
	private String estadoActaResolucion;
	private String descripcionSubEstadoActa;
	private List<ActaResponseDetalleDto> detalle;
	private List<VwPrActaLineaTiempo> lineaTiempo;
	private List<TabArchivoDownDto> archivos;
	private Integer codigoSolucionTecnologica;
	private String descripcionSolucionTecnologica;

}
