package pe.gob.onpe.presentacionbackend.model.dto.senadoresdistritomultiple;

import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroEleccionSenadoresMultipleCandidatoOrganizacionReporteDto {
	
	private Integer tipoReporte;

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
