package pe.gob.onpe.presentacionbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipanteEleccionBase {
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreAgrupacionPolitica;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String codigoAgrupacionPolitica;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String idFotoAgrupacionPolitica;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreCandidato;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dniCandidato;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String idFotoCandidato;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalVotosValidos;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalVotosEmitidos;
}
