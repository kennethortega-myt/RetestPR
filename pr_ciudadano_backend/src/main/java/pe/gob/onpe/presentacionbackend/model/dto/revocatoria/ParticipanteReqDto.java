package pe.gob.onpe.presentacionbackend.model.dto.revocatoria;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteReqDto {
	@NotNull
    private Integer idEleccion;
    @NotEmpty
    private String tipoFiltro;
    
    private Integer idAmbitoGeografico;
    private Integer ubigeoNivel1;
    private Integer ubigeoNivel2;
    private Integer ubigeoNivel3;
    private Integer codigoAgrupacionPolitica;
}
