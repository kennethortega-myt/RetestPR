package pe.gob.onpe.consultaopbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class UbigeoDepartamentoDto {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String idUbigeo;
	private String ubigeo;
	private String nombre;
}
