package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ActaResponseDetalleDto {
	private Integer nAgrupacionPolitica;
	private Integer nPosicion;
	private String cCodigo;
	private String descripcion;
	private Integer estado;
	private Integer nVotos;
	private Double nPorcentajeVotosValidos;
	private Double nPorcentajeVotosEmitidos;
	private Integer grafico;
	private int totalCandidatos;
	private List<ActaResponseDetalleCandidatoDto> candidato;
}
