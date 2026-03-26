package pe.gob.onpe.consultaopbackend.model.dto.actas;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ResumenActasObservadasResDto {
	private Long idEleccion;
	private Integer totalActas;
    private Integer contabilizadas;
    private Integer enviadasJee;
    private Date fechaActualizacion;
}
