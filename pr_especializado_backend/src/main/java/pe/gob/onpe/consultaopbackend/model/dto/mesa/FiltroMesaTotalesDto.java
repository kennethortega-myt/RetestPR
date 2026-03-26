package pe.gob.onpe.consultaopbackend.model.dto.mesa;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroMesaTotalesDto {

    @NotNull(message = "tipoFiltro es obligatorio")
    @NotEmpty(message = "tipoFiltro no debe ser vacio")
    private String tipoFiltro;
    private Integer ambitoGeografico;
    private Integer distritoElectoral;
	private Integer ubigeoNivel1;
    private Integer ubigeoNivel2;
    private Integer ubigeoNivel3;

}
