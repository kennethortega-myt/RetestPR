package pe.gob.onpe.presentacionbackend.model.dto.revocatoria;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import pe.gob.onpe.presentacionbackend.model.dto.resumengeneral.CandidatoResDto;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteResDto {
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreCandidato;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String dniCandidato;
	 
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer totalVotosValidos;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer totalVotosEmitidos;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer ubigeoNivel01;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer ubigeoNivel02;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer ubigeoNivel03;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String ubigeoDesc;
	    
	@Field("c_candidato")
	private List<CandidatoResDto> candidato;
}
