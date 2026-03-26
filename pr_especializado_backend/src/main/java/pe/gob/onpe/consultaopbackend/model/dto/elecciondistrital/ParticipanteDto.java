package pe.gob.onpe.consultaopbackend.model.dto.elecciondistrital;

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
    private String codigoAgrupacionPolitica;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreCandidato;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long totalVotosValidos;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double porcentajeVotosValidos;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double porcentajeVotosEmitidos;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer posicion;
}
