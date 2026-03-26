package pe.gob.onpe.pradminbackend.security.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateRequest {
	@NotNull(message = "La clave no puede ser nulo")
	private String clave;
	@NotNull(message = "La clave2 no puede ser nulo")
	private String clave2;
	@NotNull(message = "El tokenSasa no puede ser nulo")
	private String tokenSasa;
}
