package pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipanteCandidatoSenadoresDistritoMultipleReporteDto {
	private String codOrgPolitica;
	private String orgPolitica;
	private String nombreCandidato;
    private Integer totalVotos;
    private Integer lista;
}
