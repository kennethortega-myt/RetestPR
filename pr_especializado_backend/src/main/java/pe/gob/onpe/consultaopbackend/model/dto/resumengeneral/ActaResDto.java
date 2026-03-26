package pe.gob.onpe.consultaopbackend.model.dto.resumengeneral;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ActaResDto {

	@JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer idEleccion;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer participacionCiudadanaTotal;

    private Integer totalActas;
    private Integer contabilizadas;
    private Double contabilizadasPorcentaje;
    private Integer enviadasJee;
    private Double enviadasJeePorcentaje;
    private Integer pendientesJee;
    private Double pendientesJeePorcentaje;
    private Double participacionCiudadana;
    
    private Date fechaActualizacion;

    private Long idUbigeoDepartamento;
    private Long idUbigeoProvincia;
    private Long idUbigeoDistrito;
    private Integer idUbigeoDistritoElectoral;
    
    private Integer totalVotosEmitidos;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalElectoresHabiles;
	private Integer totalAsistentes;
	private Integer totalAusentes;
	
	public Integer getPorcentajeVotosEmitidos() {
		if (totalVotosEmitidos != null) {
            return totalVotosEmitidos == 0 ? 0 : 100;
        }
        return 0;
	}

	public Integer getPorcentajeVotosValidos() {
		if (totalVotosEmitidos != null) {
			return totalVotosEmitidos == 0 ? 0 : 100;
		}
		return 0;
	}
}
