package pe.gob.onpe.presentacionbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class UbigeoDistritoDto {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String idUbigeo;
	private String ubigeo;
	private String nombre;
}
