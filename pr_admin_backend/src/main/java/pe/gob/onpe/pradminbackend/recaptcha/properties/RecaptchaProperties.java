package pe.gob.onpe.pradminbackend.recaptcha.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record RecaptchaProperties(
		@Value("${recaptcha.siteKey}")
		String siteKey,

		@Value("${recaptcha.secretKey}")
		String secretKey,

		@Value("${recaptcha.threshold}")
		String threshold,

		@Value("${recaptcha.validar}")
		boolean validarRecaptcha)
{}
