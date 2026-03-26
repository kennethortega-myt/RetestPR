package pe.gob.onpe.consultaopbackend.model.dto.parlamentoandino;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipanteCandidatoParlamentoAndinoDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreAgrupacionPolitica;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer codigoAgrupacionPolitica;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreCandidato;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dniCandidato;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalVotosValidos;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalVotosEmitidos;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double porcentajeVotosValidos;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double porcentajeVotosEmitidos;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalCandidatos;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer lista;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer idCandidato;

}
