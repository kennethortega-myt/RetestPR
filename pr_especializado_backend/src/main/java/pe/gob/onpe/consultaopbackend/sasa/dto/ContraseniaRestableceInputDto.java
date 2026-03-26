package pe.gob.onpe.consultaopbackend.sasa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContraseniaRestableceInputDto {
	private String usuario;
	private String correo;
	private String codigoAplicacion;
}
