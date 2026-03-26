package pe.gob.onpe.consultaopbackend.model.dto.elecciondiputados;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipanteCandidatoDiputadosReporteDto {
	private String codOrgPolitica;
    private String orgPolitica;
    private String nombreCandidato;
    private Integer totalVotos;
    private Integer lista;

}
