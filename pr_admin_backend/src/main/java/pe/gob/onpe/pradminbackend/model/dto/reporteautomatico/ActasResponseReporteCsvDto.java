package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActasResponseReporteCsvDto {
	private Long id;
	private Long idEleccion;
	private String descripcionEleccion;

	private String codigoMesa;
	private String descripcionMesa;
	private Integer idAmbitoGeografico;
	private String descripcionAmbitoGeografico;

	private String ubigeoNivel01;
	private String ubigeoNivel02;
	private String ubigeoNivel03;

	private String nombreLocalVotacion;

	private Long idMesa;

	private Integer totalElectoresHabiles;
	private Integer totalVotosEmitidos;
	private Integer totalVotosValidos;

	private String estadoActa;
	private String estadoComputo;
	private String codigoEstadoActa;
	private String descripcionEstadoActa;
	
	private Integer nVotos;

	private String nombreArchivoActa;
	private List<String> nombresArchivoResoluciones;
	
	private List<String> archivoActaId;
	private List<String> archivoResolucionId;

	private List<Integer> listaVotosOrgPolitica;

}
