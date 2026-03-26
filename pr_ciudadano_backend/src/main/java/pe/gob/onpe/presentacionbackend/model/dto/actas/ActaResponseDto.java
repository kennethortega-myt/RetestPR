package pe.gob.onpe.presentacionbackend.model.dto.actas;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;
import pe.gob.onpe.presentacionbackend.model.bd.documents.VwPrActaLineaTiempo;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActaResponseDto {
	private Long id;
	private Long idMesa;
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
	private String estadoActaResolucion;
	private String estadoDescripcionActaResolucion;
	private String descripcionSubEstadoActa;
	private List<ActaResponseDetalleDto> detalle;
	private List<VwPrActaLineaTiempo> lineaTiempo;
	private List<TabArchivoDownDto> archivos;
	private Integer codigoSolucionTecnologica;
	private String descripcionSolucionTecnologica;
}
