package pe.gob.onpe.consultaopbackend.model.dto.parlamentoandino;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipanteParlamentoReporteDto {

    private String orgPolitica;
    private String codOrgPolitica;
    private Integer totalCandidatos;

    private Integer totalVotos;

    private Double votosValidos;

    private Double votosEmitidos;
    private String candidato;
    private Integer lista;

}
