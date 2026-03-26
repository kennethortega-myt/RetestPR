package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipacionTotalesResponseReporteDto {

	private Integer electoresHabiles;
	private Integer asistentes;
	private Integer ausentes;
	private Double  asistentesPorcentaje;
	private Double  ausentesPorcentaje;

	private Long idDetalleUbicacion;
	private String detalleUbicacion;
}
