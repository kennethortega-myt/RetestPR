package pe.gob.onpe.presentacionbackend.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class FiltroEleccionUbigeoReporteDto {

	private Integer tipoReporte;

    @NotNull
    private Integer idEleccion;
    @NotEmpty
    private String tipoFiltro;
    private Integer idAmbitoGeografico;
	private Integer ubigeoNivel1;
    private Integer ubigeoNivel2;
    private Integer ubigeoNivel3;

    private Integer idOrgPolitica;
    private String descripcionOrgPolitica;

    private String nombreProceso;
    private String nombreEleccion;
}
