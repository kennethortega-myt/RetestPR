package pe.gob.onpe.consultaopbackend.security.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestableceRequest {
	@NotNull(message = "El usuario no puede ser nulo")
	private String usuario;

	private String recaptcha;

}
