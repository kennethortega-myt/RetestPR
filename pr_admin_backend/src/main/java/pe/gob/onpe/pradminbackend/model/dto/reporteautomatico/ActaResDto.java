package pe.gob.onpe.pradminbackend.model.dto.reporteautomatico;

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
    private Integer porcentajeVotosEmitidos;
	private Integer porcentajeVotosValidos;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalElectoresHabiles;
	private Integer totalAsistentes;
	private Integer totalAusentes;
	
	public void calculaPorcentageTotalVotosEmitidosValidos() {
		if (totalVotosEmitidos != null) {
            this.porcentajeVotosEmitidos = totalVotosEmitidos == 0 ? 0 : 100;
            this.porcentajeVotosValidos = totalVotosEmitidos == 0 ? 0 : 100;
        } else {
            this.porcentajeVotosEmitidos = 0;
            this.porcentajeVotosValidos = 0;
        }
	}
}
