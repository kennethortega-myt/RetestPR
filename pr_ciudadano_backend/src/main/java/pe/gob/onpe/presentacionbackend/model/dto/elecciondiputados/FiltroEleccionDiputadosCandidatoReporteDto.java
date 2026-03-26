package pe.gob.onpe.presentacionbackend.model.dto.elecciondiputados;

import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroEleccionDiputadosCandidatoReporteDto {
	
	private Integer tipoReporte;

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
