package pe.gob.onpe.presentacionbackend.model.dto.actas;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
	private String cargo;
	private Integer sexo;
	private int totalCandidatos;
	private List<ActaResponseDetalleCandidatoDto> candidato;
}
