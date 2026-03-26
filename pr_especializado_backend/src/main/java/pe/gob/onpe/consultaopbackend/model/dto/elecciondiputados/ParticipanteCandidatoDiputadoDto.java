package pe.gob.onpe.consultaopbackend.model.dto.elecciondiputados;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipanteCandidatoDiputadoDto {
	
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
    private Long totalVotosValidos;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long totalVotosEmitidos;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer idCandidato;
    
    private Integer lista;
}
