package pe.gob.onpe.presentacionbackend.model.dto.revocatoria;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParticipanteDto {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer tipoEleccion;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String tipoFiltro;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long idDetUbigeoEleccion;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer ambitoGeografico;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer distritoElectoral;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer ubigeoNivel01;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer ubigeoNivel02;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer ubigeoNivel03;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String ubigeoDesc;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<ParticipanteDetalleDto> detalle;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreAgrupacionPolitica;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer codigoAgrupacionPolitica;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreCandidato;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dniCandidato;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cargo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer sexo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ParticipanteDetalleCandidatoDto> candidato;
}