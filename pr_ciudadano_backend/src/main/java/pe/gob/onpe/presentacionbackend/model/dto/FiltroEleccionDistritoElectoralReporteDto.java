package pe.gob.onpe.presentacionbackend.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class FiltroEleccionDistritoElectoralReporteDto {
	private Integer tipoReporte;
	
    @NotNull
    private Integer idEleccion;
    @NotEmpty
    private String tipoFiltro;
    
    private Integer idAmbitoGeografico;
    private Integer idDistritoElectoral;
    
    private String nombreProceso;
    private String nombreEleccion;
}
