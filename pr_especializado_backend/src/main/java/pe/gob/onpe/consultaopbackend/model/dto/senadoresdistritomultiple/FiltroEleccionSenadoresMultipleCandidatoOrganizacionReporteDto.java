package pe.gob.onpe.consultaopbackend.model.dto.senadoresdistritomultiple;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroEleccionSenadoresMultipleCandidatoOrganizacionReporteDto {

    @NotNull
    private Integer idEleccion;
    @NotEmpty
    private String tipoFiltro;
    @NotNull
    private Integer idDistritoElectoral;
    @NotNull
    private Integer idAgrupacionPolitica;
    
    private Integer idAmbitoGeografico;
    private String nombreProceso;
    private String nombreEleccion;
}
