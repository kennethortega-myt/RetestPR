package pe.gob.onpe.consultaopbackend.model.dto.resumengeneral;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ActaRespuestaReporteDto {

    private Integer totalActas;
    private Integer contabilizadas;
    private Integer enviadasJee;
    private Integer pendientesJee;

    
    private Date fechaActualizacion;

    private Integer totalElectoresHabiles;
	private Integer totalAsistentes;
	private Integer totalAusentes;

    private Integer votosOrgPolitica;

}
