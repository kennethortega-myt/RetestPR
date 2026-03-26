package pe.gob.onpe.pradminbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class UbigeoProvinciaDto {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String idUbigeo;
	private String ubigeo;
	private String nombre;
	
}
