package pe.gob.onpe.pradminbackend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoProcesoElectoral {
	ELECCIONES_GENERAL(1,"Elecciones Generales","bicameralidad"),
	ELECCIONES_REGIONALES_MUNICIPALES(2, "Elecciones regionales y municipales","elecciones-regionales-municipales"),
	REVOCATORIA(1,"Revocatoria","revocatoria");
	
	private final Integer codigo;
    private final String descripcion;
    private final String etiqueta;
}
