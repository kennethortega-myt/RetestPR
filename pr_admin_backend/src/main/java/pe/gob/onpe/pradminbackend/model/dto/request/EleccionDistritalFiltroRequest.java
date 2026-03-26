package pe.gob.onpe.pradminbackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EleccionDistritalFiltroRequest {
	private Integer idEleccion;
    private Integer idAmbitoGeografico;
    private String tipoFiltro;
}
