package pe.gob.onpe.presentacionbackend.model.dto.resumengeneral;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipanteDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreAgrupacionPolitica;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer codigoAgrupacionPolitica;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String idFotoAgrupacionPolitica;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreCandidato;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dniCandidato;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalVotosValidos;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double porcentajeVotosValidos;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double porcentajeVotosEmitidos;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cargo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ParticipanteCandidatoDto> candidato;
}