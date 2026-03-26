package pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipanteDiputadosReporteDto {
	private String codOrgPolitica;
	private Integer nAgrupacionPolitica;
    private String orgPolitica;
    private Integer totalVotos;
    private Double votosValidos;
    private Double votosEmitidos;

}
