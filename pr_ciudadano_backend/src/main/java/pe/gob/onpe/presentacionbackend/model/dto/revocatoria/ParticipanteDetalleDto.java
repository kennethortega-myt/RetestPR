package pe.gob.onpe.presentacionbackend.model.dto.revocatoria;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipanteDetalleDto {
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreAgrupacionPolitica;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer codigoAgrupacionPolitica;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreCandidato;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dniCandidato;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cargo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ParticipanteDetalleCandidatoDto> candidato;
}
