package pe.gob.onpe.consultaopbackend.model.dto.elecciondiputados;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroEleccionDiputadosCandidatoReporteDto {

    @NotNull
    private Integer idEleccion;
    @NotEmpty
    private String tipoFiltro;
    
    private Integer idAgrupacionPolitica;
    private Integer idAmbitoGeografico;
    private Integer idDistritoElectoral;
    
    private String nombreProceso;
    private String nombreEleccion;
}
