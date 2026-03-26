package pe.gob.onpe.consultaopbackend.model.dto.eleccionpresidencial;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipantePresidencialReporteDto {

    private String orgPolitica;
    private String codOrgPolitica;
    private String candidato;

    private Integer totalVotos;

    private Double votosValidos;

    private Double votosEmitidos;

}
