package pe.gob.onpe.pradminbackend.recaptcha.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record SeguridadValidarProperties(
		@Value("${seguridad.validar}")
		String validar
		)
{}