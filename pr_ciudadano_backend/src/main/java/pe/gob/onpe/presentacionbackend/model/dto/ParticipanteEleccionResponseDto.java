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
public class ParticipanteEleccionResponseDto extends ParticipanteEleccionBase {
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer idAgrupacionPolitica;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double porcentajeVotosValidos;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double porcentajeVotosEmitidos;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Integer posicion;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalCandidatos;
}
