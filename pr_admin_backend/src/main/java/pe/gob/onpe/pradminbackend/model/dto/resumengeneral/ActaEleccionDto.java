package pe.gob.onpe.pradminbackend.model.dto.resumengeneral;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ActaEleccionDto {

	@JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer idEleccion;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalElectoresHabiles;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer participacionCiudadanaTotal;

    private Double actasContabilizadas;
    private Integer contabilizadas;
    private Integer totalActas;
    private Double participacionCiudadana;
    private Double actasEnviadasJee;
    private Integer enviadasJee;
    private Double actasPendientesJee;
    private Integer pendientesJee;
    private Date fechaActualizacion;

    private Long idUbigeoDepartamento;
    private Long idUbigeoProvincia;
    private Long idUbigeoDistrito;
    private Integer idUbigeoDistritoElectoral;
    
    private Integer totalVotosEmitidos;
    private Integer totalVotosValidos;
    private Integer porcentajeVotosEmitidos;
	private Integer porcentajeVotosValidos;

}
