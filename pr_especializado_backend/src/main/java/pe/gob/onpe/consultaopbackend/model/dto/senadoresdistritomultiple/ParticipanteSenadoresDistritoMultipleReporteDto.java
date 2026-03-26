package pe.gob.onpe.consultaopbackend.model.dto.senadoresdistritomultiple;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipanteSenadoresDistritoMultipleReporteDto {
	private String codOrgPolitica;
	private Integer nAgrupacionPolitica;
    private String orgPolitica;
    private Integer totalVotos;
    private Double votosValidos;
    private Double votosEmitidos;
}
