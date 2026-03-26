package pe.gob.onpe.pradminbackend.model.dto.response.resoluciones;

import lombok.Data;
import pe.gob.onpe.pradminbackend.model.dto.request.resoluciones.ResolucionAsociadosRequest;

import java.io.Serializable;
import java.util.List;
@Data
public class ResumenResolucionesDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private int numResolucionesAplicadas;
    private int numResolucionesSinAplicar;
    private Long numTotalResoluciones;
    private List<ResolucionAsociadosRequest> resoluciones;
}
