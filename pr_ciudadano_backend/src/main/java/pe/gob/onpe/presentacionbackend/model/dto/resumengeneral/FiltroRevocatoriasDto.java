package pe.gob.onpe.presentacionbackend.model.dto.resumengeneral;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiltroRevocatoriasDto {
	private Integer idEleccion;

    private Integer idAmbitoGeografico;
    private String tipoFiltro;
}
