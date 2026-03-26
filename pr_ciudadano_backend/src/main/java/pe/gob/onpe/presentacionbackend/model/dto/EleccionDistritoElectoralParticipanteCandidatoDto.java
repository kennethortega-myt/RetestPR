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
public abstract class EleccionDistritoElectoralParticipanteCandidatoDto extends ParticipanteEleccionBase {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer idCandidato;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer lista;
}
